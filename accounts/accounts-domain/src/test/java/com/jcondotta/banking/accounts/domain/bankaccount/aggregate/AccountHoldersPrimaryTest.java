package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountHoldersPrimaryTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER = AccountHolderFixtures.PATRIZIO;

  @Test
  void shouldReturnPrimaryHolder_whenPrimaryExists() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);
    var jointHolder = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER);

    var accountHolders = AccountHolders.of(primaryHolder, jointHolder);

    assertThat(accountHolders.primary())
      .isEqualTo(primaryHolder);
  }
}