package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.shared.AggregateKeyTemplate;

public final class BankAccountEntityKey {

  public static final String BANK_ACCOUNT_PK_TEMPLATE = AggregateKeyTemplate.BANK_ACCOUNT;
  public static final String BANK_ACCOUNT_SK_TEMPLATE = AggregateKeyTemplate.BANK_ACCOUNT;

  private BankAccountEntityKey() {
  }

  public static String partitionKey(BankAccountId bankAccountId) {
    return BANK_ACCOUNT_PK_TEMPLATE.formatted(bankAccountId.value().toString());
  }

  public static String sortKey(BankAccountId bankAccountId) {
    return BANK_ACCOUNT_SK_TEMPLATE.formatted(bankAccountId.value().toString());
  }
}