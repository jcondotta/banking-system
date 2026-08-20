package com.jcondotta.banking.recipients.application.recipient.command.remove;

import com.jcondotta.application.command.Command;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;

import static java.util.Objects.requireNonNull;

public record RemoveRecipientCommand(BankAccountId bankAccountId, RecipientId recipientId)
  implements Command<Void> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";
  static final String RECIPIENT_ID_REQUIRED = "recipientId must be provided";

  public RemoveRecipientCommand {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
    requireNonNull(recipientId, RECIPIENT_ID_REQUIRED);
  }
}