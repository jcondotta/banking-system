package com.jcondotta.banking.accounts.outbox.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static com.jcondotta.banking.accounts.infrastructure.support.Preconditions.positive;

@ConfigurationProperties(prefix = "app.outbox.polling")
public record OutboxPollingProperties(Duration interval) {

  static final String INTERVAL_MUST_BE_POSITIVE = "polling.interval must be greater than zero";

  public OutboxPollingProperties {
    positive(interval, INTERVAL_MUST_BE_POSITIVE);
  }
}
