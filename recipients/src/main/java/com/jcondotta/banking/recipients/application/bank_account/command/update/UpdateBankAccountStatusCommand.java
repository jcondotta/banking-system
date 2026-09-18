package com.jcondotta.banking.recipients.application.bank_account.command.update;

import com.jcondotta.application.command.Command;
import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import static java.util.Objects.requireNonNull;

public record UpdateBankAccountStatusCommand(
  BankAccountId bankAccountId,
  BankAccountStatus status
) implements Command {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";
  static final String STATUS_REQUIRED = "status must be provided";

  public UpdateBankAccountStatusCommand {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
    requireNonNull(status, STATUS_REQUIRED);
  }
}
