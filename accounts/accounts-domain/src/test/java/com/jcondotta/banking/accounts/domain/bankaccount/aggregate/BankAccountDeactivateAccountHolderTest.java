package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.AccountHolderNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.CannotDeactivatePrimaryHolderException;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountDeactivateAccountHolderTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER = AccountHolderFixtures.PATRIZIO;

  private static final Instant ACCOUNT_CREATED_AT = TimeTestFactory.FIXED_INSTANT;

  @Test
  void shouldDeactivateJointAccountHolder_whenAccountHolderIsJoint() {
    var primary = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);
    var joint = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    var bankAccount = BankAccount.restore(
      BankAccountId.newId(),
      AccountType.CHECKING,
      Currency.EUR,
      BankAccountTestFixture.VALID_IBAN,
      AccountStatus.ACTIVE,
      ACCOUNT_CREATED_AT,
      AccountHolders.of(primary, joint)
    );

    bankAccount.deactivateHolder(joint.getId());

    assertThat(joint.isActive()).isFalse();
  }

  @Test
  void shouldThrowException_whenTryingToDeactivatePrimaryAccountHolder() {
    var primary = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    var bankAccount = BankAccount.restore(
      BankAccountId.newId(),
      AccountType.CHECKING,
      Currency.EUR,
      BankAccountTestFixture.VALID_IBAN,
      AccountStatus.ACTIVE,
      ACCOUNT_CREATED_AT,
      AccountHolders.of(primary)
    );

    assertThatThrownBy(() -> bankAccount.deactivateHolder(primary.getId()))
      .isInstanceOf(CannotDeactivatePrimaryHolderException.class)
      .hasMessage(CannotDeactivatePrimaryHolderException.PRIMARY_ACCOUNT_HOLDER_CANNOT_BE_DEACTIVATED);
  }

  @Test
  void shouldThrowException_whenAccountHolderDoesNotExist() {
    var primary = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    var bankAccount = BankAccount.restore(
      BankAccountId.newId(),
      AccountType.CHECKING,
      Currency.EUR,
      BankAccountTestFixture.VALID_IBAN,
      AccountStatus.ACTIVE,
      ACCOUNT_CREATED_AT,
      AccountHolders.of(primary)
    );

    assertThatThrownBy(() -> bankAccount.deactivateHolder(AccountHolderId.newId()))
      .isInstanceOf(AccountHolderNotFoundException.class);
  }
}