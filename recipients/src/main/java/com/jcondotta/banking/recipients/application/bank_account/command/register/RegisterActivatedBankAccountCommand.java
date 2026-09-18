package com.jcondotta.banking.recipients.application.bank_account.command.register;

import com.jcondotta.application.command.Command;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import static java.util.Objects.requireNonNull;

public record RegisterActivatedBankAccountCommand(BankAccountId bankAccountId) implements Command {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";

  public RegisterActivatedBankAccountCommand {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
  }
}
