package com.jcondotta.banking.accounts.infrastructure.fixtures;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolder;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;

import java.time.Instant;

public final class BankAccountTestFixture {

  public static final Iban VALID_IBAN = Iban.of("ES3801283316232166447417");

  public static final AccountType DEFAULT_ACCOUNT_TYPE = AccountType.CHECKING;

  public static final Currency DEFAULT_CURRENCY = Currency.EUR;

  private BankAccountTestFixture() {
  }

  public static BankAccount openPendingAccount(AccountHolderFixtures holder) {
    return openPendingAccount(holder, DEFAULT_ACCOUNT_TYPE, DEFAULT_CURRENCY);
  }

  public static BankAccount openPendingAccount(AccountHolderFixtures holder, AccountType accountType, Currency currency) {
    return BankAccount.open(
      holder.getAccountHolderName(),
      holder.getIdentityDocument(),
      holder.getDateOfBirth(),
      holder.getEmail(),
      accountType,
      currency,
      VALID_IBAN
    );
  }

  public static BankAccount openActiveAccount(AccountHolderFixtures holder) {
    return openActiveAccount(holder, DEFAULT_ACCOUNT_TYPE, DEFAULT_CURRENCY);
  }

  public static BankAccount openActiveAccount(AccountHolderFixtures holder, AccountType accountType, Currency currency) {
    var account = openPendingAccount(holder, accountType, currency);
    account.activate();
    account.pullEvents();

    return account;
  }

  public static AccountHolder createPrimaryHolder(AccountHolderFixtures fixture, Instant createdAt) {
    return BankAccount.restoreAccountHolder(
      AccountHolderId.newId(),
      fixture.getAccountHolderName(),
      fixture.getIdentityDocument(),
      fixture.getDateOfBirth(),
      fixture.getEmail(),
      HolderType.PRIMARY,
      createdAt
    );
  }

  public static AccountHolder createJointHolder(AccountHolderFixtures fixture, Instant createdAt) {
    return BankAccount.restoreAccountHolder(
      AccountHolderId.newId(),
      fixture.getAccountHolderName(),
      fixture.getIdentityDocument(),
      fixture.getDateOfBirth(),
      fixture.getEmail(),
      HolderType.JOINT,
      createdAt
    );
  }
}