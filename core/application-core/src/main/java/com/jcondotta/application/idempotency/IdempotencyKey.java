package com.jcondotta.application.idempotency;

import java.util.Objects;
import java.util.UUID;

public record IdempotencyKey(UUID value) {

  public IdempotencyKey {
    Objects.requireNonNull(value, "Idempotency key must be provided");
  }

  public static IdempotencyKey of(UUID value) {
    return new IdempotencyKey(value);
  }
}
