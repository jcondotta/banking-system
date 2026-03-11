package com.jcondotta.banking.accounts.application.bankaccount.command.close.model;

import com.jcondotta.application.core.Command;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;

import static java.util.Objects.requireNonNull;

public record CloseBankAccountCommand(BankAccountId bankAccountId) implements Command<Void> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";

  public CloseBankAccountCommand {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
  }
}