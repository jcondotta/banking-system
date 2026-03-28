package com.jcondotta.banking.recipients.application.bankaccount.command.register;

import com.jcondotta.application.core.command.Command;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import static java.util.Objects.requireNonNull;

public record RegisterBankAccountCommand(BankAccountId bankAccountId)
  implements Command<Void> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";

  public RegisterBankAccountCommand {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
  }
}