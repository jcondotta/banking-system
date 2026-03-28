package com.jcondotta.banking.accounts.infrastructure.properties;

import jakarta.validation.constraints.NotNull;

import java.time.Duration;

public record PollingProperties(

  @NotNull Duration interval

) {

  static final String INTERVAL_MUST_NOT_BE_NULL = "polling.interval must not be null";
  static final String INTERVAL_MUST_BE_POSITIVE = "polling.interval must be > 0";

  public PollingProperties {
    if (interval == null) {
      throw new IllegalArgumentException(INTERVAL_MUST_NOT_BE_NULL);
    }
    if (interval.isZero() || interval.isNegative()) {
      throw new IllegalArgumentException(INTERVAL_MUST_BE_POSITIVE);
    }
  }
}
