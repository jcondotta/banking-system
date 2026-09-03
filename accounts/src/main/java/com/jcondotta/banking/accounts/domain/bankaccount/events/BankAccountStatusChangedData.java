package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountStatusChangedData(AccountStatus previousStatus, AccountStatus currentStatus) {

  public BankAccountStatusChangedData {
    required(previousStatus, BankAccountErrors.PREVIOUS_STATUS_MUST_BE_PROVIDED);
    required(currentStatus, BankAccountErrors.CURRENT_STATUS_MUST_BE_PROVIDED);
  }
}
