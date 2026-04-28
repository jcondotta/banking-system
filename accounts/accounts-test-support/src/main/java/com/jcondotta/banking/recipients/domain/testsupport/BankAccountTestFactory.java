package com.jcondotta.banking.recipients.domain.testsupport;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolder;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolders;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;

import java.time.Instant;

public final class BankAccountTestFactory {

  private static final Iban DEFAULT_IBAN = BankAccountFixture.VALID_IBAN;

  private BankAccountTestFactory() {}

  public static BankAccount build(BankAccountId id, AccountType accountType, Currency currency, Iban iban,
                                  AccountStatus status, Instant createdAt, AccountHolders accountHolders) {
    return BankAccount.restore(id, accountType, currency, iban, status, createdAt, accountHolders);
  }

  public static BankAccount build(BankAccountId id, AccountType accountType, Currency currency,
                                  AccountStatus status, Instant createdAt, AccountHolders accountHolders) {
    return build(id, accountType, currency, DEFAULT_IBAN, status, createdAt, accountHolders);
  }

  public static BankAccount build(BankAccountId id, AccountType accountType, Currency currency,
                                  AccountStatus status, Instant createdAt, AccountHolder... holders) {
    return build(id, accountType, currency, status, createdAt, AccountHolders.of(holders));
  }

  public static BankAccount build(BankAccountId id, AccountType accountType, Currency currency,
                                  AccountStatus status, AccountHolder... holders) {
    return build(id, accountType, currency, status, Instant.now(), AccountHolders.of(holders));
  }

  public static BankAccount build(BankAccountId id, AccountType accountType, Currency currency,
                                  AccountHolder... holders) {
    return build(id, accountType, currency, AccountStatus.ACTIVE, Instant.now(), AccountHolders.of(holders));
  }

  public static BankAccount withPrimary(BankAccountId id, AccountType accountType, Currency currency,
                                        AccountHolderFixtures fixture) {
    return build(id, accountType, currency, AccountHolderTestFactory.primary(fixture));
  }

  public static BankAccount withPrimary(AccountHolderFixtures fixture) {
    return build(BankAccountId.newId(), AccountType.CHECKING, Currency.EUR, AccountHolderTestFactory.primary(fixture));
  }

  public static BankAccount withPrimaryAndJoint(BankAccountId id, AccountType accountType, Currency currency,
                                                AccountHolderFixtures primaryFixture,
                                                AccountHolderFixtures jointFixture) {
    return build(id, accountType, currency,
      AccountHolderTestFactory.primary(primaryFixture),
      AccountHolderTestFactory.joint(jointFixture));
  }

  public static BankAccount withPrimaryAndJoint(AccountHolderFixtures primaryFixture, AccountHolderFixtures jointFixture) {
    return withPrimaryAndJoint(BankAccountId.newId(), AccountType.CHECKING, Currency.EUR, primaryFixture, jointFixture);
  }
}