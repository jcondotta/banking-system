package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.MaxJointHoldersExceededException;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountHoldersAddTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER_1 = AccountHolderFixtures.PATRIZIO;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER_2 = AccountHolderFixtures.VIRGINIO;

  @Test
  void shouldAddJointHolder_whenJointHolderIsValid() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);
    var jointHolder = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER_1);

    var accountHolders = AccountHolders.of(primaryHolder);

    accountHolders.add(jointHolder);

    assertThat(accountHolders.joint())
      .containsExactly(jointHolder);
  }

  @Test
  void shouldThrowException_whenMaxJointHoldersExceeded() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);
    var jointHolder1 = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER_1);
    var jointHolder2 = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER_2);

    var accountHolders = AccountHolders.of(primaryHolder, jointHolder1);

    assertThatThrownBy(() -> accountHolders.add(jointHolder2))
      .isInstanceOf(MaxJointHoldersExceededException.class);
  }

  @Test
  void shouldThrowException_whenTryingToAddPrimaryHolder() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);
    var anotherPrimary = BankAccountTestFixture.createPrimaryHolder(AccountHolderFixtures.VIRGINIO);

    var accountHolders = AccountHolders.of(primaryHolder);

    assertThatThrownBy(() -> accountHolders.add(anotherPrimary))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldThrowException_whenHolderIsNull() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);
    var accountHolders = AccountHolders.of(primaryHolder);

    assertThatThrownBy(() -> accountHolders.add(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage("Account holder must not be null");
  }
}