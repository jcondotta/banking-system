package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider.AccountTypeAndCurrencyArgumentsProvider;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountHoldersConfigurationException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.MaxJointHoldersExceededException;
import com.jcondotta.banking.accounts.domain.bankaccount.factory.ClockTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountRestoreTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT_ACCOUNT_HOLDER = AccountHolderFixtures.PATRIZIO;

  private static final Iban VALID_IBAN = BankAccountTestFixture.VALID_IBAN;
  private static final AccountType ACCOUNT_TYPE_SAVINGS = AccountType.SAVINGS;
  private static final Currency CURRENCY_USD = Currency.USD;

  private static final Instant ACCOUNT_CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @ParameterizedTest
  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
  void shouldRestoreBankAccountWithPrimaryAccountHolder_whenValidDataProvided(AccountType accountType, Currency currency) {
    var primaryAccountHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    var bankAccountId = BankAccountId.newId();
    var bankAccount = BankAccount.restore(
      bankAccountId,
      accountType,
      currency,
      VALID_IBAN,
      AccountStatus.ACTIVE,
      ACCOUNT_CREATED_AT,
      AccountHolders.of(primaryAccountHolder)
    );

    assertThat(bankAccount).isNotNull();
    assertThat(bankAccount.getId()).isEqualTo(bankAccountId);
    assertThat(bankAccount.getAccountType()).isEqualTo(accountType);
    assertThat(bankAccount.getCurrency()).isEqualTo(currency);
    assertThat(bankAccount.getIban()).isEqualTo(BankAccountTestFixture.VALID_IBAN);
    assertThat(bankAccount.getAccountStatus().isActive()).isTrue();
    assertThat(bankAccount.getCreatedAt()).isEqualTo(ACCOUNT_CREATED_AT);
    assertThat(bankAccount.pullEvents()).isEmpty();
    assertThat(bankAccount.getActiveHolders())
      .hasSize(1)
      .containsExactly(primaryAccountHolder);
  }

  @ParameterizedTest
  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
  void shouldRestoreBankAccountWithPrimaryAndJointAccountHolders_whenValidDataProvided(AccountType accountType, Currency currency) {
    var primaryAccountHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);
    var jointAccountHolder = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    var bankAccountId = BankAccountId.newId();
    var bankAccount = BankAccount.restore(
      bankAccountId,
      accountType,
      currency,
      BankAccountTestFixture.VALID_IBAN,
      AccountStatus.ACTIVE,
      ACCOUNT_CREATED_AT,
      AccountHolders.of(jointAccountHolder, primaryAccountHolder)
    );

    assertThat(bankAccount).isNotNull();
    assertThat(bankAccount.getId()).isEqualTo(bankAccountId);
    assertThat(bankAccount.getAccountType()).isEqualTo(accountType);
    assertThat(bankAccount.getCurrency()).isEqualTo(currency);
    assertThat(bankAccount.getIban()).isEqualTo(BankAccountTestFixture.VALID_IBAN);
    assertThat(bankAccount.getAccountStatus().isActive()).isTrue();
    assertThat(bankAccount.getCreatedAt()).isEqualTo(ACCOUNT_CREATED_AT);
    assertThat(bankAccount.pullEvents()).isEmpty();
    assertThat(bankAccount.getActiveHolders())
      .hasSize(2)
      .containsExactly(primaryAccountHolder, jointAccountHolder)
      .extracting(AccountHolder::getAccountHolderType)
      .containsExactly(HolderType.PRIMARY, HolderType.JOINT);
  }

  @Test
  void shouldReturnOnlyActiveAccountHolders_whenAnyAccountHolderIsDeactivated() {
    var primaryAccountHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);
    var jointAccountHolder = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    var bankAccount = BankAccount.restore(
      BankAccountId.newId(),
      AccountType.CHECKING,
      Currency.EUR,
      BankAccountTestFixture.VALID_IBAN,
      AccountStatus.ACTIVE,
      ACCOUNT_CREATED_AT,
      AccountHolders.of(primaryAccountHolder, jointAccountHolder)
    );

    jointAccountHolder.deactivate();

    assertThat(bankAccount.getActiveHolders())
      .containsExactly(primaryAccountHolder);
  }

  @Test
  void shouldThrowInvalidBankAccountHoldersConfigurationException_whenRestoringWithoutAnyAccountHolders() {
    assertThatThrownBy(() ->
      BankAccount.restore(
        BankAccountId.newId(),
        ACCOUNT_TYPE_SAVINGS,
        CURRENCY_USD,
        BankAccountTestFixture.VALID_IBAN,
        AccountStatus.ACTIVE,
        ACCOUNT_CREATED_AT,
        AccountHolders.of()
      ))
      .isInstanceOf(InvalidBankAccountHoldersConfigurationException.class);
  }

  @Test
  void shouldThrowInvalidBankAccountHoldersConfigurationException_whenRestoringWithOnlyJointAccountHolder() {
    var jointAccountHolder = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    assertThatThrownBy(() ->
      BankAccount.restore(
        BankAccountId.newId(),
        ACCOUNT_TYPE_SAVINGS,
        CURRENCY_USD,
        BankAccountTestFixture.VALID_IBAN,
        AccountStatus.ACTIVE,
        ACCOUNT_CREATED_AT,
        AccountHolders.of(jointAccountHolder)
      ))
      .isInstanceOf(InvalidBankAccountHoldersConfigurationException.class);
  }

  @Test
  void shouldThrowException_whenRestoringWithMultiplePrimaryAccountHolders() {
    var primaryAccountHolder1 = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);
    var primaryAccountHolder2 = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    assertThatThrownBy(() ->
      BankAccount.restore(
        BankAccountId.newId(),
        ACCOUNT_TYPE_SAVINGS,
        CURRENCY_USD,
        BankAccountTestFixture.VALID_IBAN,
        AccountStatus.ACTIVE,
        ACCOUNT_CREATED_AT,
        AccountHolders.of(primaryAccountHolder1, primaryAccountHolder2)
      ))
      .isInstanceOf(InvalidBankAccountHoldersConfigurationException.class);
  }

  @Test
  void shouldThrowMaxJointAccountHoldersExceededException_whenRestoringWithPrimaryAndMultipleJointAccountHolders_ifOnlyOneJointIsAllowed() {
    var primaryAccountHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);
    var jointAccountHolder1 = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);
    var jointAccountHolder2 = BankAccountTestFixture.createJointHolder(JOINT_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    assertThatThrownBy(() ->
      BankAccount.restore(
        BankAccountId.newId(),
        ACCOUNT_TYPE_SAVINGS,
        CURRENCY_USD,
        BankAccountTestFixture.VALID_IBAN,
        AccountStatus.ACTIVE,
        ACCOUNT_CREATED_AT,
        AccountHolders.of(primaryAccountHolder, jointAccountHolder1, jointAccountHolder2)
      ))
      .isInstanceOf(MaxJointHoldersExceededException.class);
  }
}
