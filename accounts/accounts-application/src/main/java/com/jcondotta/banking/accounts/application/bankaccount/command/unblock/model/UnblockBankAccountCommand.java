package com.jcondotta.banking.accounts.application.bankaccount.command.unblock.model;

import com.jcondotta.application.core.Command;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;

import static java.util.Objects.requireNonNull;

public record UnblockBankAccountCommand(BankAccountId bankAccountId) implements Command<Void> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";

  public UnblockBankAccountCommand {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
  }
}