package com.jcondotta.banking.accounts.infrastructure.properties;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;

public record ProcessingProperties(

  @NotNull
  Duration timeout

) {

  static final String TIMEOUT_MUST_NOT_BE_NULL = "processing.timeout must not be null";
  static final String TIMEOUT_MUST_BE_POSITIVE = "processing.timeout must be > 0";

  public ProcessingProperties {
    if (timeout == null) {
      throw new IllegalArgumentException(TIMEOUT_MUST_NOT_BE_NULL);
    }
    if (timeout.isZero() || timeout.isNegative()) {
      throw new IllegalArgumentException(TIMEOUT_MUST_BE_POSITIVE);
    }
  }
}
