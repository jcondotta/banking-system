package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.shared.AggregateKeyTemplate;

public final class AccountHolderEntityKey {

  public static final String ACCOUNT_HOLDER_PK_TEMPLATE = AggregateKeyTemplate.BANK_ACCOUNT;
  public static final String ACCOUNT_HOLDER_SK_TEMPLATE = "ACCOUNT_HOLDER#%s";

  private AccountHolderEntityKey() {
  }

  public static String partitionKey(BankAccountId bankAccountId) {
    return ACCOUNT_HOLDER_PK_TEMPLATE.formatted(bankAccountId.value().toString());
  }

  public static String sortKey(AccountHolderId accountHolderId) {
    return ACCOUNT_HOLDER_SK_TEMPLATE.formatted(accountHolderId.value().toString());
  }
}