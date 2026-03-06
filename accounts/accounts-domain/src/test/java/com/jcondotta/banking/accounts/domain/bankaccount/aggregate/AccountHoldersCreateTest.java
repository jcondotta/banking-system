package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountHoldersConfigurationException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.MaxJointHoldersExceededException;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountHoldersCreateTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER_1 = AccountHolderFixtures.PATRIZIO;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER_2 = AccountHolderFixtures.VIRGINIO;

  @Test
  void shouldCreateAccountHolders_whenConfigurationIsValid() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);
    var jointHolder = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER_1);

    var accountHolders = AccountHolders.of(primaryHolder, jointHolder);

    assertThat(accountHolders.primary()).isEqualTo(primaryHolder);
    assertThat(accountHolders.joint()).containsExactly(jointHolder);
  }

  @Test
  void shouldThrowException_whenNoPrimaryHolderExists() {
    var jointHolder = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER_1);

    assertThatThrownBy(() -> AccountHolders.of(jointHolder))
      .isInstanceOf(InvalidBankAccountHoldersConfigurationException.class);
  }

  @Test
  void shouldThrowException_whenMoreThanOnePrimaryHolderExists() {
    var primary1 = BankAccountTestFixture.createPrimaryHolder(AccountHolderFixtures.JEFFERSON);
    var primary2 = BankAccountTestFixture.createPrimaryHolder(AccountHolderFixtures.VIRGINIO);

    assertThatThrownBy(() -> AccountHolders.of(primary1, primary2))
      .isInstanceOf(InvalidBankAccountHoldersConfigurationException.class);
  }

  @Test
  void shouldThrowException_whenMaxJointExceeded() {
    var primary = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);
    var joint1 = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER_1);
    var joint2 = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER_2);

    assertThatThrownBy(() -> AccountHolders.of(primary, joint1, joint2))
      .isInstanceOf(MaxJointHoldersExceededException.class);
  }

  @Test
  void shouldThrowException_whenHoldersListIsNull() {
    assertThatThrownBy(() -> AccountHolders.of((List<AccountHolder>) null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolders.ACCOUNT_HOLDERS_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenVarargsIsNull() {
    assertThatThrownBy(() -> AccountHolders.of((AccountHolder[]) null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(AccountHolders.ACCOUNT_HOLDERS_MUST_BE_PROVIDED);
  }
}