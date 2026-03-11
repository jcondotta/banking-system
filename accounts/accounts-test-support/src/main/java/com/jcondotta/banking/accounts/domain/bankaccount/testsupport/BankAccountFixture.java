package com.jcondotta.banking.accounts.domain.bankaccount.testsupport;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolder;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;

import java.time.Instant;

public final class BankAccountFixture {

  public static final Iban VALID_IBAN = Iban.of("ES3801283316232166447417");

  public static final AccountType DEFAULT_ACCOUNT_TYPE = AccountType.CHECKING;

  public static final Currency DEFAULT_CURRENCY = Currency.EUR;

  private BankAccountFixture() {
  }

  public static BankAccount openPendingAccount(AccountHolderFixtures fixtures) {
    return openPendingAccount(fixtures, DEFAULT_ACCOUNT_TYPE, DEFAULT_CURRENCY);
  }

  public static BankAccount openPendingAccount(AccountHolderFixtures fixtures, AccountType accountType, Currency currency) {
    return BankAccount.open(
      BankAccountId.newId(),
      fixtures.personalInfo(),
      fixtures.contactInfo(),
      fixtures.address(),
      accountType,
      currency,
      VALID_IBAN
    );
  }

  public static BankAccount openActiveAccount(AccountHolderFixtures fixtures) {
    return openActiveAccount(fixtures, DEFAULT_ACCOUNT_TYPE, DEFAULT_CURRENCY);
  }

  public static BankAccount openActiveAccount(AccountHolderFixtures fixtures, AccountType accountType, Currency currency) {
    var account = openPendingAccount(fixtures, accountType, currency);
    account.activate();
    account.pullEvents();

    return account;
  }

  public static AccountHolder createPrimaryHolder(AccountHolderFixtures fixtures, Instant createdAt) {
    return AccountHolder.restore(
      AccountHolderId.newId(),
      fixtures.personalInfo(),
      fixtures.contactInfo(),
      fixtures.address(),
      HolderType.PRIMARY,
      createdAt
    );
  }

  public static AccountHolder createJointHolder(AccountHolderFixtures fixtures, Instant createdAt) {
    return AccountHolder.restore(
      AccountHolderId.newId(),
      fixtures.personalInfo(),
      fixtures.contactInfo(),
      fixtures.address(),
      HolderType.JOINT,
      createdAt
    );
  }
}