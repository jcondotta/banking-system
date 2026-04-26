package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.exception.DomainConflictException;

public final class RecipientAlreadyExistsException extends DomainConflictException {

  public static final String RECIPIENT_ALREADY_EXISTS = "Recipient with id '%s' already exists";

  public RecipientAlreadyExistsException(RecipientId recipientId) {
    super(RECIPIENT_ALREADY_EXISTS.formatted(recipientId.value()));
  }
}
