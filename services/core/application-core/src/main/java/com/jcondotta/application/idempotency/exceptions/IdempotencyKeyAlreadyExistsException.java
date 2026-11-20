package com.jcondotta.application.idempotency.exceptions;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;

public final class IdempotencyKeyAlreadyExistsException extends RuntimeException {

  private final IdempotencyKey key;

  public IdempotencyKeyAlreadyExistsException(IdempotencyKey key) {
    super("Idempotency key already exists: " + key.value());
    this.key = key;
  }

  public IdempotencyKey key() {
    return key;
  }
}
