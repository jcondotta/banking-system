package com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient;

import com.jcondotta.application.core.command.Command;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;

import static java.util.Objects.requireNonNull;

public record CreateRecipientCommand(
  BankAccountId bankAccountId,
  RecipientName recipientName,
  Iban iban
) implements Command<RecipientId> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";
  static final String RECIPIENT_NAME_REQUIRED = "recipientName must be provided";
  static final String IBAN_REQUIRED = "iban must be provided";

  public CreateRecipientCommand {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
    requireNonNull(recipientName, RECIPIENT_NAME_REQUIRED);
    requireNonNull(iban, IBAN_REQUIRED);
  }
}