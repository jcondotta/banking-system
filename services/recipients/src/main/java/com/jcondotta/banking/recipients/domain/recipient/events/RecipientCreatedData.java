package com.jcondotta.banking.recipients.domain.recipient.events;

import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;

import java.util.UUID;

import static com.jcondotta.domain.support.Preconditions.required;
import static com.jcondotta.domain.support.Preconditions.requiredNotBlank;

public record RecipientCreatedData(UUID bankAccountId, String name, String iban) {

  public RecipientCreatedData {
    required(bankAccountId, RecipientError.BANK_ACCOUNT_ID_NOT_PROVIDED);
    requiredNotBlank(name, RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
    requiredNotBlank(iban, RecipientError.IBAN_NOT_PROVIDED);
  }
}
