package com.jcondotta.banking.accounts.application.bankaccount.command.block.model;

import com.jcondotta.application.core.command.Command;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;

import static java.util.Objects.requireNonNull;

public record BlockBankAccountCommand(BankAccountId bankAccountId) implements Command<Void> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";

  public BlockBankAccountCommand {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
  }
}