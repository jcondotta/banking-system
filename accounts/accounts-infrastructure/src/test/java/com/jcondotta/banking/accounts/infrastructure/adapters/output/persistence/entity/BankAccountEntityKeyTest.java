package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountEntityKeyTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final BankAccountId BANK_ACCOUNT_ID = new BankAccountId(BANK_ACCOUNT_UUID);

  @Test
  void shouldReturnFormattedPartitionKey_whenBankAccountIdIsValid() {
    var partitionKey = BankAccountEntityKey.partitionKey(BANK_ACCOUNT_ID);

    assertThat(partitionKey).isEqualTo("BANK_ACCOUNT#" + BANK_ACCOUNT_UUID);
  }

  @Test
  void shouldReturnFormattedSortKey_whenBankAccountIdIsValid() {
    var sortKey = BankAccountEntityKey.sortKey(BANK_ACCOUNT_ID);

    assertThat(sortKey).isEqualTo("BANK_ACCOUNT#" + BANK_ACCOUNT_UUID);
  }

  @Test
  void shouldReturnDistinctKeys_whenDifferentBankAccountIdsAreProvided() {
    var anotherBankAccountId = new BankAccountId(UUID.randomUUID());

    var partitionKey = BankAccountEntityKey.partitionKey(BANK_ACCOUNT_ID);
    var anotherPartitionKey = BankAccountEntityKey.partitionKey(anotherBankAccountId);

    assertThat(partitionKey).isNotEqualTo(anotherPartitionKey);
  }
}