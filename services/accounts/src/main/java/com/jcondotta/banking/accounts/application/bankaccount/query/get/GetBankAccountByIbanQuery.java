package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.application.query.Query;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;

import static java.util.Objects.requireNonNull;

public record GetBankAccountByIbanQuery(Iban iban)
  implements Query<BankAccountSummary> {

  static final String IBAN_REQUIRED = "iban must be provided";

  public GetBankAccountByIbanQuery {
    requireNonNull(iban, IBAN_REQUIRED);
  }
}
