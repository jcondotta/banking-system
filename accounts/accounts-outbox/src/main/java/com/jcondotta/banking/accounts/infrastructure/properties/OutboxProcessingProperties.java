package com.jcondotta.banking.accounts.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static com.jcondotta.banking.accounts.infrastructure.support.Preconditions.*;

@ConfigurationProperties(prefix = "app.outbox.processing")
public record OutboxProcessingProperties(Duration acquireTimeout, Duration publishTimeout, Lease lease, int maxRetries) {

  static final String ACQUIRE_TIMEOUT_MUST_BE_POSITIVE = "processing.acquireTimeout must be greater than zero";

  static final String PUBLISH_TIMEOUT_MUST_BE_POSITIVE = "processing.publishTimeout must be greater than zero";

  static final String LEASE_MUST_NOT_BE_NULL = "processing.lease must be provided";

  static final String MAX_RETRIES_MUST_BE_POSITIVE = "processing.maxRetries must be greater than zero";

  static final String PUBLISH_TIMEOUT_MUST_BE_LESS_THAN_ACQUIRE = "processing.publishTimeout must be less than processing.acquireTimeout";

  static final String ACQUIRE_TIMEOUT_MUST_BE_LESS_THAN_LEASE = "processing.acquireTimeout must be less than processing.lease.duration";

  static final String PROCESSING_WINDOW_MUST_BE_LESS_THAN_LEASE =
    "processing.lease.duration must exceed processing.acquireTimeout + processing.publishTimeout";

  public OutboxProcessingProperties {
    positive(acquireTimeout, ACQUIRE_TIMEOUT_MUST_BE_POSITIVE);
    positive(publishTimeout, PUBLISH_TIMEOUT_MUST_BE_POSITIVE);
    required(lease, LEASE_MUST_NOT_BE_NULL);
    checkArgument(maxRetries > 0, MAX_RETRIES_MUST_BE_POSITIVE);

    checkArgument(publishTimeout.compareTo(acquireTimeout) < 0, PUBLISH_TIMEOUT_MUST_BE_LESS_THAN_ACQUIRE);
    checkArgument(acquireTimeout.compareTo(lease.duration()) < 0, ACQUIRE_TIMEOUT_MUST_BE_LESS_THAN_LEASE);
    checkArgument(acquireTimeout.plus(publishTimeout).compareTo(lease.duration()) < 0, PROCESSING_WINDOW_MUST_BE_LESS_THAN_LEASE);
  }

  public record Lease(Duration duration) {

    static final String LEASE_DURATION_MUST_BE_POSITIVE = "processing.lease.duration must be greater than zero";

    public Lease {
      positive(duration, LEASE_DURATION_MUST_BE_POSITIVE);
    }
  }
}