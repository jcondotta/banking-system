package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountJointHolderDeactivatedData(AccountHolderId accountHolderId) {

  public BankAccountJointHolderDeactivatedData {
    required(accountHolderId, AccountHolderId.ACCOUNT_HOLDER_ID_NOT_PROVIDED);
  }
}
