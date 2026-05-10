package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountHoldersFindTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER = AccountHolderFixtures.PATRIZIO;

  @Test
  void shouldReturnAccountHolder_whenHolderExists() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);

    var accountHolders = AccountHolders.of(primaryHolder);

    assertThat(accountHolders.find(primaryHolder.getId()))
      .isPresent()
      .contains(primaryHolder);
  }

  @Test
  void shouldReturnEmptyOptional_whenHolderDoesNotExist() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);

    var accountHolders = AccountHolders.of(primaryHolder);

    assertThat(accountHolders.find(AccountHolderId.newId()))
      .isEmpty();
  }
}