package com.jcondotta.application.idempotency.exceptions;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;

public final class IdempotencyKeyConflictException extends RuntimeException {

  public IdempotencyKeyConflictException(IdempotencyKey key) {
    super("Idempotency key was reused with different request data: " + key.value());
  }
}
