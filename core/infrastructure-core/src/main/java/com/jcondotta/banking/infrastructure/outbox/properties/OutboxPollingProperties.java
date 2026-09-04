package com.jcondotta.banking.infrastructure.outbox.properties;

import com.jcondotta.domain.support.Preconditions;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.outbox.polling")
public record OutboxPollingProperties(Duration interval) {

  static final String INTERVAL_MUST_BE_POSITIVE = "polling.interval must be greater than zero";

  public OutboxPollingProperties {
    Preconditions.checkArgument(interval.isPositive(), INTERVAL_MUST_BE_POSITIVE);
  }
}
