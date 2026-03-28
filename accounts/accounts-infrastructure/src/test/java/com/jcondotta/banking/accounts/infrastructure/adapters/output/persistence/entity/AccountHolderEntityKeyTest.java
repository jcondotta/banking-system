package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountHolderEntityKeyTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final BankAccountId BANK_ACCOUNT_ID = new BankAccountId(BANK_ACCOUNT_UUID);

  private static final UUID ACCOUNT_HOLDER_UUID = UUID.randomUUID();
  private static final AccountHolderId ACCOUNT_HOLDER_ID = new AccountHolderId(ACCOUNT_HOLDER_UUID);

  @Test
  void shouldReturnFormattedPartitionKey_whenBankAccountIdIsValid() {
    var partitionKey = AccountHolderEntityKey.partitionKey(BANK_ACCOUNT_ID);

    assertThat(partitionKey).isEqualTo("BANK_ACCOUNT#" + BANK_ACCOUNT_UUID);
  }

  @Test
  void shouldReturnFormattedSortKey_whenAccountHolderIdIsValid() {
    var sortKey = AccountHolderEntityKey.sortKey(ACCOUNT_HOLDER_ID);

    assertThat(sortKey).isEqualTo("ACCOUNT_HOLDER#" + ACCOUNT_HOLDER_UUID);
  }

  @Test
  void shouldReturnDistinctPartitionKeys_whenDifferentBankAccountIdsAreProvided() {
    var anotherBankAccountId = new BankAccountId(UUID.randomUUID());

    var partitionKey = AccountHolderEntityKey.partitionKey(BANK_ACCOUNT_ID);
    var anotherPartitionKey = AccountHolderEntityKey.partitionKey(anotherBankAccountId);

    assertThat(partitionKey).isNotEqualTo(anotherPartitionKey);
  }

  @Test
  void shouldReturnDistinctSortKeys_whenDifferentAccountHolderIdsAreProvided() {
    var anotherAccountHolderId = new AccountHolderId(UUID.randomUUID());

    var sortKey = AccountHolderEntityKey.sortKey(ACCOUNT_HOLDER_ID);
    var anotherSortKey = AccountHolderEntityKey.sortKey(anotherAccountHolderId);

    assertThat(sortKey).isNotEqualTo(anotherSortKey);
  }
}