package com.jcondotta.banking.accounts.infrastructure.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.outbox.processing")
public record OutboxProcessingProperties(

  @NotNull
  Duration timeout

) {

  static final String TIMEOUT_MUST_NOT_BE_NULL = "processing.timeout must not be null";
  static final String TIMEOUT_MUST_BE_POSITIVE = "processing.timeout must be > 0";

  public OutboxProcessingProperties {
    if (timeout == null) {
      throw new IllegalArgumentException(TIMEOUT_MUST_NOT_BE_NULL);
    }
    if (timeout.isZero() || timeout.isNegative()) {
      throw new IllegalArgumentException(TIMEOUT_MUST_BE_POSITIVE);
    }
  }
}
