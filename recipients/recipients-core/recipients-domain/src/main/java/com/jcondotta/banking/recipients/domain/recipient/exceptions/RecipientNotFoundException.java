package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public final class RecipientNotFoundException extends DomainNotFoundException {

  public static final String MESSAGE = "Recipient not found";

  private final String recipientId;

  public RecipientNotFoundException(RecipientId recipientId) {
    super(MESSAGE);
    this.recipientId = recipientId.value().toString();
  }

  public String getRecipientId() {
    return recipientId;
  }
}
