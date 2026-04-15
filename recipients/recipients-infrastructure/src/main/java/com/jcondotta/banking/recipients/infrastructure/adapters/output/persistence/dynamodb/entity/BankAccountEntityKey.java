package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

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