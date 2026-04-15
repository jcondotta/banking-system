package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.mapper;

import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RecipientEntityMapperTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  private final RecipientEntityMapper mapper = new RecipientEntityMapper();

  @Test
  void shouldMapRecipientToEntity_whenRecipientIsProvided() {
    var recipient = RecipientFixtures.JEFFERSON.create();

    var entity = mapper.toEntity(BANK_ACCOUNT_ID, recipient);

    assertAll(
      () -> assertThat(entity.getEntityType()).isEqualTo(EntityType.RECIPIENT),
      () -> assertThat(entity.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID.value()),
      () -> assertThat(entity.getRecipientId()).isEqualTo(recipient.getId().value()),
      () -> assertThat(entity.getRecipientName()).isEqualTo(recipient.getRecipientName().value()),
      () -> assertThat(entity.getIban()).isEqualTo(recipient.getIban().value()),
      () -> assertThat(entity.getRecipientStatus()).isEqualTo(RecipientStatus.ACTIVE.name()),
      () -> assertThat(entity.getCreatedAt()).isEqualTo(recipient.getCreatedAt())
    );
  }

  @Test
  void shouldRestoreRecipient_whenEntityIsProvided() {
    var recipient = RecipientFixtures.JEFFERSON.create();
    var entity = mapper.toEntity(BANK_ACCOUNT_ID, recipient);

    var restoredRecipient = mapper.toDomain(entity);

    assertAll(
      () -> assertThat(restoredRecipient.getId()).isEqualTo(recipient.getId()),
      () -> assertThat(restoredRecipient.getRecipientName()).isEqualTo(recipient.getRecipientName()),
      () -> assertThat(restoredRecipient.getIban()).isEqualTo(recipient.getIban()),
      () -> assertThat(restoredRecipient.getStatus()).isEqualTo(recipient.getStatus()),
      () -> assertThat(restoredRecipient.getCreatedAt()).isEqualTo(recipient.getCreatedAt())
    );
  }
}
