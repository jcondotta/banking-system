package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.query;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import com.jcondotta.banking.recipients.infrastructure.support.DynamoPageIterable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipientQueryRepositoryImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

  @Mock
  private DynamoDbTable<AccountRecipientEntity> accountRecipientsTable;

  private RecipientQueryRepositoryImpl repository;
  private final RecipientSummaryEntityMapper recipientSummaryEntityMapper = new RecipientSummaryEntityMapper();

  @BeforeEach
  void setUp() {
    repository = new RecipientQueryRepositoryImpl(accountRecipientsTable, recipientSummaryEntityMapper);
  }

  @Test
  void shouldReturnActiveRecipients_whenBankAccountHasActiveRecipients() {
    var recipientId = RecipientId.newId();
    var bankAccountEntity = bankAccountEntity();
    var recipientEntity = recipientEntity(recipientId, "Jefferson Condotta", "ES3801283316232166447417", RecipientStatus.ACTIVE);

    when(accountRecipientsTable.query(any(QueryConditional.class)))
      .thenReturn(DynamoPageIterable.pageOf(List.of(bankAccountEntity, recipientEntity)));

    var result = repository.findActiveByBankAccountId(BANK_ACCOUNT_ID);

    assertThat(result)
      .containsExactly(new RecipientSummary(
        recipientId.value(),
        "Jefferson Condotta",
        "ES3801283316232166447417",
        CREATED_AT
      ));
  }

  @Test
  void shouldReturnOnlyActiveRecipients_whenBankAccountHasActiveAndRemovedRecipients() {
    var activeRecipientId = RecipientId.newId();
    var removedRecipientId = RecipientId.newId();
    var entities = List.of(
      bankAccountEntity(),
      recipientEntity(activeRecipientId, "Jefferson Condotta", "ES3801283316232166447417", RecipientStatus.ACTIVE),
      recipientEntity(removedRecipientId, "Patrizio Condotta", "IT93Q0300203280175171887193", RecipientStatus.REMOVED)
    );

    when(accountRecipientsTable.query(any(QueryConditional.class)))
      .thenReturn(DynamoPageIterable.pageOf(entities));

    var result = repository.findActiveByBankAccountId(BANK_ACCOUNT_ID);

    assertThat(result)
      .singleElement()
      .extracting(RecipientSummary::recipientId)
      .isEqualTo(activeRecipientId.value());
  }

  @Test
  void shouldReturnEmptyList_whenBankAccountHasNoRecipients() {
    when(accountRecipientsTable.query(any(QueryConditional.class)))
      .thenReturn(DynamoPageIterable.pageOf(List.of(bankAccountEntity())));

    var result = repository.findActiveByBankAccountId(BANK_ACCOUNT_ID);

    assertThat(result).isEmpty();
  }

  @Test
  void shouldThrowException_whenBankAccountDoesNotExist() {
    when(accountRecipientsTable.query(any(QueryConditional.class)))
      .thenReturn(DynamoPageIterable.emptyPage());

    assertThatThrownBy(() -> repository.findActiveByBankAccountId(BANK_ACCOUNT_ID))
      .isInstanceOf(BankAccountNotFoundException.class)
      .hasMessage(new BankAccountNotFoundException(BANK_ACCOUNT_ID).getMessage());
  }

  @Test
  void shouldThrowException_whenOnlyRecipientOrphanEntityExists() {
    var recipientEntity = recipientEntity(
      RecipientId.newId(),
      "Jefferson Condotta",
      "ES3801283316232166447417",
      RecipientStatus.ACTIVE
    );

    when(accountRecipientsTable.query(any(QueryConditional.class)))
      .thenReturn(DynamoPageIterable.pageOf(List.of(recipientEntity)));

    assertThatThrownBy(() -> repository.findActiveByBankAccountId(BANK_ACCOUNT_ID))
      .isInstanceOf(BankAccountNotFoundException.class);
  }

  private static AccountRecipientEntity bankAccountEntity() {
    return AccountRecipientEntity.builder()
      .entityType(EntityType.BANK_ACCOUNT)
      .bankAccountId(BANK_ACCOUNT_ID.value())
      .accountStatus(AccountStatus.ACTIVE.name())
      .build();
  }

  private static AccountRecipientEntity recipientEntity(
    RecipientId recipientId,
    String recipientName,
    String iban,
    RecipientStatus status
  ) {
    return AccountRecipientEntity.builder()
      .entityType(EntityType.RECIPIENT)
      .bankAccountId(BANK_ACCOUNT_ID.value())
      .recipientId(recipientId.value())
      .recipientName(recipientName)
      .iban(iban)
      .recipientStatus(status.name())
      .createdAt(CREATED_AT)
      .build();
  }
}
