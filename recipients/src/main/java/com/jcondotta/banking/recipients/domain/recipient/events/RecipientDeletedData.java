package com.jcondotta.banking.recipients.domain.recipient.events;

import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;

import java.util.UUID;

import static com.jcondotta.domain.support.Preconditions.required;

public record RecipientDeletedData(UUID bankAccountId) {

  public RecipientDeletedData {
    required(bankAccountId, RecipientError.BANK_ACCOUNT_ID_NOT_PROVIDED);
  }
}
