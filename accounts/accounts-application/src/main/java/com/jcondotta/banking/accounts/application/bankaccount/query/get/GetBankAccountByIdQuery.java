package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.application.core.query.Query;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;

import static java.util.Objects.requireNonNull;

public record GetBankAccountByIdQuery(BankAccountId bankAccountId)
  implements Query<BankAccountSummary> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";

  public GetBankAccountByIdQuery {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
  }
}