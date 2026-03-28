package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.persistence.entity;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientEntityKeyTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final BankAccountId BANK_ACCOUNT_ID = new BankAccountId(BANK_ACCOUNT_UUID);

  private static final UUID RECIPIENT_UUID = UUID.randomUUID();
  private static final RecipientId RECIPIENT_ID = new RecipientId(RECIPIENT_UUID);

  @Test
  void shouldReturnFormattedPartitionKey_whenBankAccountIdIsValid() {
    var partitionKey = RecipientEntityKey.partitionKey(BANK_ACCOUNT_ID);

    assertThat(partitionKey).isEqualTo("BANK_ACCOUNT#" + BANK_ACCOUNT_UUID);
  }

  @Test
  void shouldReturnFormattedSortKey_whenRecipientIdIsValid() {
    var sortKey = RecipientEntityKey.sortKey(RECIPIENT_ID);

    assertThat(sortKey).isEqualTo("RECIPIENT#" + RECIPIENT_UUID);
  }

  @Test
  void shouldReturnDistinctPartitionKeys_whenDifferentBankAccountIdsAreProvided() {
    var anotherBankAccountId = new BankAccountId(UUID.randomUUID());

    var partitionKey = RecipientEntityKey.partitionKey(BANK_ACCOUNT_ID);
    var anotherPartitionKey = RecipientEntityKey.partitionKey(anotherBankAccountId);

    assertThat(partitionKey).isNotEqualTo(anotherPartitionKey);
  }

  @Test
  void shouldReturnDistinctSortKeys_whenDifferentRecipientIdsAreProvided() {
    var anotherRecipientId = new RecipientId(UUID.randomUUID());

    var sortKey = RecipientEntityKey.sortKey(RECIPIENT_ID);
    var anotherSortKey = RecipientEntityKey.sortKey(anotherRecipientId);

    assertThat(sortKey).isNotEqualTo(anotherSortKey);
  }
}