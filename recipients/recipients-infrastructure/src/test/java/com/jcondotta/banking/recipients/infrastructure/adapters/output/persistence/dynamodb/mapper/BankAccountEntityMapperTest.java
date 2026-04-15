package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.mapper;

import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.BankAccountFixtures;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class BankAccountEntityMapperTest {

  private final RecipientEntityMapper recipientEntityMapper = new RecipientEntityMapper();

  private final BankAccountEntityMapper mapper = new BankAccountEntityMapper(recipientEntityMapper);

  @Test
  void shouldMapBankAccountToEntities_whenBankAccountHasRecipients() {
    var bankAccount = BankAccountFixtures.WITH_TWO_RECIPIENTS.create();

    var entities = mapper.toEntities(bankAccount);

    assertThat(entities).hasSize(3);
    assertThat(entities)
      .extracting(AccountRecipientEntity::getEntityType)
      .containsExactly(EntityType.BANK_ACCOUNT, EntityType.RECIPIENT, EntityType.RECIPIENT);

    var bankAccountEntity = entities.getFirst();
    assertAll(
      () -> assertThat(bankAccountEntity.getBankAccountId()).isEqualTo(bankAccount.getId().value()),
      () -> assertThat(bankAccountEntity.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE.name()),
      () -> assertThat(bankAccountEntity.isBankAccount()).isTrue()
    );
  }

  @Test
  void shouldMapOnlyBankAccountEntity_whenBankAccountHasNoRecipients() {
    var bankAccount = BankAccountFixtures.WITH_NO_RECIPIENTS.create();

    var entities = mapper.toEntities(bankAccount);

    assertThat(entities)
      .singleElement()
      .satisfies(entity -> assertAll(
        () -> assertThat(entity.getEntityType()).isEqualTo(EntityType.BANK_ACCOUNT),
        () -> assertThat(entity.getBankAccountId()).isEqualTo(bankAccount.getId().value()),
        () -> assertThat(entity.isBankAccount()).isTrue()
      ));
  }

  @Test
  void shouldRestoreBankAccount_whenEntitiesAreProvided() {
    var bankAccount = BankAccountFixtures.WITH_TWO_RECIPIENTS.create();
    var entities = mapper.toEntities(bankAccount);

    var restoredBankAccount = mapper.restore(entities);

    assertAll(
      () -> assertThat(restoredBankAccount.getId()).isEqualTo(bankAccount.getId()),
      () -> assertThat(restoredBankAccount.getAccountStatus()).isEqualTo(bankAccount.getAccountStatus()),
      () -> assertThat(restoredBankAccount.getActiveRecipients())
        .containsExactlyElementsOf(bankAccount.getActiveRecipients())
    );
  }

  @Test
  void shouldThrowException_whenBankAccountEntityIsMissing() {
    var recipientEntity = AccountRecipientEntity.builder()
      .entityType(EntityType.RECIPIENT)
      .bankAccountId(UUID.randomUUID())
      .build();

    assertThatThrownBy(() -> mapper.restore(List.of(recipientEntity)))
      .isInstanceOf(IllegalStateException.class)
      .hasMessage("Bank account entity not found");
  }

  @Test
  void shouldRestoreBankAccountWithoutRecipients_whenOnlyBankAccountEntityIsProvided() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());
    var entity = AccountRecipientEntity.builder()
      .entityType(EntityType.BANK_ACCOUNT)
      .bankAccountId(bankAccountId.value())
      .accountStatus(AccountStatus.ACTIVE.name())
      .build();

    BankAccount bankAccount = mapper.restore(List.of(entity));

    assertAll(
      () -> assertThat(bankAccount.getId()).isEqualTo(bankAccountId),
      () -> assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE),
      () -> assertThat(bankAccount.getActiveRecipients()).isEmpty()
    );
  }

  @Test
  void shouldIncludeRemovedRecipientInEntities_whenRecipientHasBeenRemoved() {
    var bankAccount = BankAccountFixtures.WITH_ONE_RECIPIENT.create();
    var recipient = bankAccount.getActiveRecipients().getFirst();
    bankAccount.removeRecipient(recipient.getId());

    var entities = mapper.toEntities(bankAccount);

    assertThat(entities).hasSize(2);

    var recipientEntity = entities.stream()
      .filter(AccountRecipientEntity::isRecipient)
      .findFirst()
      .orElseThrow();

    assertThat(recipientEntity.getRecipientId()).isEqualTo(recipient.getId().value());
    assertThat(recipientEntity.getRecipientStatus()).isEqualTo(RecipientStatus.REMOVED.name());
  }

  @Test
  void shouldRestoreRecipientAsRemoved_whenSavedAfterRemoval() {
    var bankAccount = BankAccountFixtures.WITH_ONE_RECIPIENT.create();
    var recipient = bankAccount.getActiveRecipients().getFirst();
    bankAccount.removeRecipient(recipient.getId());

    var entities = mapper.toEntities(bankAccount);
    var restored = mapper.restore(entities);

    assertThat(restored.getActiveRecipients()).isEmpty();
    assertThat(restored.getRecipients())
      .singleElement()
      .satisfies(r -> assertAll(
        () -> assertThat(r.getId()).isEqualTo(recipient.getId()),
        () -> assertThat(r.isActive()).isFalse(),
        () -> assertThat(r.getStatus()).isEqualTo(RecipientStatus.REMOVED)
      ));
  }
}
