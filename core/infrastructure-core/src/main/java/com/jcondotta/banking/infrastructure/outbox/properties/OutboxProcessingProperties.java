package com.jcondotta.banking.infrastructure.outbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static com.jcondotta.domain.support.Preconditions.checkArgument;

@ConfigurationProperties(prefix = "app.outbox.processing")
public record OutboxProcessingProperties(Duration acquireTimeout, Duration publishTimeout, Duration claimTimeout, int maxRetries) {

  static final String ACQUIRE_TIMEOUT_MUST_BE_POSITIVE = "processing.acquireTimeout must be greater than zero";

  static final String PUBLISH_TIMEOUT_MUST_BE_POSITIVE = "processing.publishTimeout must be greater than zero";

  static final String CLAIM_TIMEOUT_MUST_BE_POSITIVE = "processing.claimTimeout must be greater than zero";

  static final String MAX_RETRIES_MUST_BE_POSITIVE = "processing.maxRetries must be greater than zero";

  static final String PUBLISH_TIMEOUT_MUST_BE_LESS_THAN_ACQUIRE = "processing.publishTimeout must be less than processing.acquireTimeout";

  static final String ACQUIRE_TIMEOUT_MUST_BE_LESS_THAN_CLAIM = "processing.acquireTimeout must be less than processing.claimTimeout";

  static final String PROCESSING_WINDOW_MUST_BE_LESS_THAN_CLAIM =
    "processing.claimTimeout must exceed processing.acquireTimeout + processing.publishTimeout";

  public OutboxProcessingProperties {
    checkArgument(acquireTimeout.isPositive(), ACQUIRE_TIMEOUT_MUST_BE_POSITIVE);
    checkArgument(publishTimeout.isPositive(), PUBLISH_TIMEOUT_MUST_BE_POSITIVE);
    checkArgument(claimTimeout.isPositive(), CLAIM_TIMEOUT_MUST_BE_POSITIVE);
    checkArgument(maxRetries > 0, MAX_RETRIES_MUST_BE_POSITIVE);

    checkArgument(publishTimeout.compareTo(acquireTimeout) < 0, PUBLISH_TIMEOUT_MUST_BE_LESS_THAN_ACQUIRE);
    checkArgument(acquireTimeout.compareTo(claimTimeout) < 0, ACQUIRE_TIMEOUT_MUST_BE_LESS_THAN_CLAIM);
    checkArgument(acquireTimeout.plus(publishTimeout).compareTo(claimTimeout) < 0, PROCESSING_WINDOW_MUST_BE_LESS_THAN_CLAIM);
  }
}
