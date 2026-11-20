package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountOpenedData(
  AccountType accountType,
  Currency currency,
  AccountHolderId primaryAccountHolderId
) {

  public BankAccountOpenedData {
    required(accountType, BankAccountErrors.ACCOUNT_TYPE_MUST_BE_PROVIDED);
    required(currency, BankAccountErrors.CURRENCY_MUST_BE_PROVIDED);
    required(primaryAccountHolderId, AccountHolderId.ACCOUNT_HOLDER_ID_NOT_PROVIDED);
  }
}
