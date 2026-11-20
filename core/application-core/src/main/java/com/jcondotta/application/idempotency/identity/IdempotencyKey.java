package com.jcondotta.application.idempotency.identity;

import java.util.Objects;
import java.util.UUID;

public record IdempotencyKey(UUID value) {

  public IdempotencyKey {
    Objects.requireNonNull(value, "Idempotency key must be provided");
  }

  public static IdempotencyKey of(UUID value) {
    return new IdempotencyKey(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
