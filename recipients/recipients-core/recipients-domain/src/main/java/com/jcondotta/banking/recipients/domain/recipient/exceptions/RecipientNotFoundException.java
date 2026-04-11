package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public final class RecipientNotFoundException extends DomainNotFoundException {

  public static final String RECIPIENT_NOT_FOUND = "Recipient with id '%s' was not found";

  public RecipientNotFoundException(RecipientId recipientId) {
    super(RECIPIENT_NOT_FOUND.formatted(recipientId));
  }
}