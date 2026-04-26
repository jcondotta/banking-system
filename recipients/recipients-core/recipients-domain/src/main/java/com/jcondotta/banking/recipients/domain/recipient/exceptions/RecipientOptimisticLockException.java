package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.exception.DomainConflictException;

import java.util.UUID;

public final class RecipientOptimisticLockException extends DomainConflictException {

  public static final String RECIPIENT_CONCURRENT_MODIFICATION = "Recipient was modified concurrently — please reload and retry";

  private final UUID recipientId;

  public RecipientOptimisticLockException(RecipientId recipientId) {
    super(RECIPIENT_CONCURRENT_MODIFICATION);
    this.recipientId = recipientId.value();
  }

  public UUID getRecipientId() {
    return recipientId;
  }
}
