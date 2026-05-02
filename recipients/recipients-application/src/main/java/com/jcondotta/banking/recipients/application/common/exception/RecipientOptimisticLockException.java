package com.jcondotta.banking.recipients.application.common.exception;

import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.common.FailureReasonProvider;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.exception.DomainConflictException;

import java.util.UUID;

public final class RecipientOptimisticLockException extends DomainConflictException implements FailureReasonProvider {

  public static final String RECIPIENT_CONCURRENT_MODIFICATION =
    "Recipient was modified concurrently - please reload and retry";

  private final UUID recipientId;

  public RecipientOptimisticLockException(RecipientId recipientId) {
    super(RECIPIENT_CONCURRENT_MODIFICATION);
    this.recipientId = recipientId.value();
  }

  @Override
  public FailureReason reason() {
    return FailureReason.OPTIMISTIC_LOCK_CONFLICT;
  }

  public UUID getRecipientId() {
    return recipientId;
  }
}
