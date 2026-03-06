package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.AccountHolderNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.CannotDeactivatePrimaryHolderException;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountHoldersDeactivateTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER = AccountHolderFixtures.PATRIZIO;

  @Test
  void shouldDeactivateJointHolder_whenJointHolderExists() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);
    var jointHolder = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER);

    var accountHolders = AccountHolders.of(primaryHolder, jointHolder);

    accountHolders.deactivate(jointHolder.getId());

    assertThat(jointHolder.isActive()).isFalse();
  }

  @Test
  void shouldThrowException_whenHolderDoesNotExist() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);

    var accountHolders = AccountHolders.of(primaryHolder);

    assertThatThrownBy(() -> accountHolders.deactivate(AccountHolderId.newId()))
      .isInstanceOf(AccountHolderNotFoundException.class);
  }

  @Test
  void shouldThrowException_whenTryingToDeactivatePrimaryHolder() {
    var primaryHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER);

    var accountHolders = AccountHolders.of(primaryHolder);

    assertThatThrownBy(() -> accountHolders.deactivate(primaryHolder.getId()))
      .isInstanceOf(CannotDeactivatePrimaryHolderException.class);
  }
}