package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;

class BankAccountNullValidationTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;

  private static final Iban VALID_IBAN = BankAccountTestFixture.VALID_IBAN;
  private static final AccountType ACCOUNT_TYPE_SAVINGS = AccountType.SAVINGS;
  private static final Currency CURRENCY_USD = Currency.USD;

//  @Test
//  void shouldThrowNullPointerException_whenAccountHolderNameIsNull() {
//    assertThatThrownBy(() ->
//      BankAccount.open(
//        null,
//        PRIMARY_ACCOUNT_HOLDER.getIdentityDocument(),
//        PRIMARY_ACCOUNT_HOLDER.getDateOfBirth(),
//        PRIMARY_ACCOUNT_HOLDER.contactInfo(),
//        ACCOUNT_TYPE_SAVINGS,
//        CURRENCY_USD,
//        VALID_IBAN
//      ))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage(AccountHolderValidationErrors.NAME_NOT_NULL);
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenPassportNumberIsNull() {
//    assertThatThrownBy(() ->
//      BankAccount.open(
//        PRIMARY_ACCOUNT_HOLDER.getName(),
//        null,
//        PRIMARY_ACCOUNT_HOLDER.getDateOfBirth(),
//        PRIMARY_ACCOUNT_HOLDER.contactInfo(),
//        ACCOUNT_TYPE_SAVINGS,
//        CURRENCY_USD,
//        VALID_IBAN
//      ))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage(AccountHolderValidationErrors.IDENTITY_DOCUMENT_NOT_NULL);
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenDateOfBirthIsNull() {
//    assertThatThrownBy(() ->
//      BankAccount.open(
//        PRIMARY_ACCOUNT_HOLDER.getName(),
//        PRIMARY_ACCOUNT_HOLDER.getIdentityDocument(),
//        null,
//        PRIMARY_ACCOUNT_HOLDER.contactInfo(),
//        ACCOUNT_TYPE_SAVINGS,
//        CURRENCY_USD,
//        VALID_IBAN
//      ))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage(AccountHolderValidationErrors.DATE_OF_BIRTH_NOT_NULL);
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenEmailIsNull() {
//    assertThatThrownBy(() ->
//      BankAccount.open(
//        PRIMARY_ACCOUNT_HOLDER.getName(),
//        PRIMARY_ACCOUNT_HOLDER.getIdentityDocument(),
//        PRIMARY_ACCOUNT_HOLDER.getDateOfBirth(),
//        null,
//        ACCOUNT_TYPE_SAVINGS,
//        CURRENCY_USD,
//        VALID_IBAN
//      ))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage(AccountHolderValidationErrors.EMAIL_NOT_NULL);
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenAccountTypeIsNull() {
//    assertThatThrownBy(() ->
//      BankAccount.open(
//        PRIMARY_ACCOUNT_HOLDER.getName(),
//        PRIMARY_ACCOUNT_HOLDER.getIdentityDocument(),
//        PRIMARY_ACCOUNT_HOLDER.getDateOfBirth(),
//        PRIMARY_ACCOUNT_HOLDER.contactInfo(),
//        null,
//        CURRENCY_USD,
//        VALID_IBAN
//      ))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage(BankAccountValidationErrors.ACCOUNT_TYPE_NOT_NULL);
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenCurrencyIsNull() {
//    assertThatThrownBy(() ->
//      BankAccount.open(
//        PRIMARY_ACCOUNT_HOLDER.getName(),
//        PRIMARY_ACCOUNT_HOLDER.getIdentityDocument(),
//        PRIMARY_ACCOUNT_HOLDER.getDateOfBirth(),
//        PRIMARY_ACCOUNT_HOLDER.contactInfo(),
//        ACCOUNT_TYPE_SAVINGS,
//        null,
//        VALID_IBAN
//      ))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage(BankAccountValidationErrors.CURRENCY_NOT_NULL);
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenIbanIsNull() {
//    assertThatThrownBy(() ->
//      BankAccount.open(
//        PRIMARY_ACCOUNT_HOLDER.getName(),
//        PRIMARY_ACCOUNT_HOLDER.getIdentityDocument(),
//        PRIMARY_ACCOUNT_HOLDER.getDateOfBirth(),
//        PRIMARY_ACCOUNT_HOLDER.contactInfo(),
//        ACCOUNT_TYPE_SAVINGS,
//        CURRENCY_USD,
//        null
//      ))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage(BankAccountValidationErrors.IBAN_NOT_NULL);
//  }
}