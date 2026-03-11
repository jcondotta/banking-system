package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;

public final class BankAccountEntityKey {

  public static final String BANK_ACCOUNT_PK_TEMPLATE = "BANK_ACCOUNT#%s";
  public static final String BANK_ACCOUNT_SK_TEMPLATE = "BANK_ACCOUNT#%s";

  private BankAccountEntityKey() {
  }

  public static String partitionKey(BankAccountId bankAccountId) {
    return BANK_ACCOUNT_PK_TEMPLATE.formatted(bankAccountId.value().toString());
  }

  public static String sortKey(BankAccountId bankAccountId) {
    return BANK_ACCOUNT_SK_TEMPLATE.formatted(bankAccountId.value().toString());
  }
}