package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;

public final class RecipientAlreadyExistsException extends RecipientDomainConflictException {

  public static final String RECIPIENT_ALREADY_EXISTS = "Recipient with id '%s' already exists";

  public RecipientAlreadyExistsException(RecipientId recipientId) {
    super(RECIPIENT_ALREADY_EXISTS.formatted(recipientId.value()));
  }

  @Override
  public FailureReason reason() {
    return FailureReason.ALREADY_EXISTS;
  }
}
