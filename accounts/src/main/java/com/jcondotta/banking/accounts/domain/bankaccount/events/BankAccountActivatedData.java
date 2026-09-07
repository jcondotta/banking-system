package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountActivatedData(String iban, Currency currency) {

  public BankAccountActivatedData {
    required(iban, BankAccountErrors.IBAN_MUST_BE_PROVIDED);
    required(currency, BankAccountErrors.CURRENCY_MUST_BE_PROVIDED);
  }
}
