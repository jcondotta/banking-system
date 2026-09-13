package com.jcondotta.banking.infrastructure.outbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jcondotta.domain.support.Preconditions.checkArgument;
import static com.jcondotta.domain.support.Preconditions.required;

@ConfigurationProperties(prefix = "app.outbox")
public record OutboxProperties(Shards shards, Worker worker) {

  public record Shards(int count) {

    static final String COUNT_MUST_BE_POSITIVE = "shards.count must be greater than zero";

    public Shards {
      checkArgument(count > 0, COUNT_MUST_BE_POSITIVE);
    }

    public Set<Integer> shardIds() {
      return IntStream.range(0, count)
        .boxed()
        .collect(Collectors.toUnmodifiableSet());
    }
  }

  public record Worker(boolean enabled, Polling polling, Processing processing, Retry retry) {

    public Worker {
      if (enabled) {
        required(polling, "polling must be configured when app.outbox.worker.enabled=true");
        required(processing, "processing must be configured when app.outbox.worker.enabled=true");
        required(retry, "retry must be configured when app.outbox.worker.enabled=true");
      }
    }

    public record Polling(Duration interval) {

      static final String INTERVAL_MUST_BE_PROVIDED = "polling.interval must not be null";
      static final String INTERVAL_MUST_BE_POSITIVE  = "polling.interval must be greater than zero";

      public Polling {
        required(interval, INTERVAL_MUST_BE_PROVIDED);
        checkArgument(interval.isPositive(), INTERVAL_MUST_BE_POSITIVE);
      }
    }

    public record Processing(
      int batchSizePerShard,
      int concurrencyPerShard,
      Duration acquireTimeout,
      Duration publishTimeout,
      Duration claimTimeout
    ) {

      static final String BATCH_SIZE_PER_SHARD_MUST_BE_POSITIVE =
        "processing.batchSizePerShard must be greater than zero";
      static final String CONCURRENCY_PER_SHARD_MUST_BE_POSITIVE =
        "processing.concurrencyPerShard must be greater than zero";
      static final String BATCH_SIZE_MUST_BE_AT_LEAST_CONCURRENCY =
        "processing.batchSizePerShard must be >= processing.concurrencyPerShard";
      static final String ACQUIRE_TIMEOUT_MUST_BE_POSITIVE =
        "processing.acquireTimeout must be greater than zero";
      static final String PUBLISH_TIMEOUT_MUST_BE_POSITIVE =
        "processing.publishTimeout must be greater than zero";
      static final String CLAIM_TIMEOUT_MUST_BE_POSITIVE =
        "processing.claimTimeout must be greater than zero";
      static final String PUBLISH_TIMEOUT_MUST_BE_LESS_THAN_ACQUIRE =
        "processing.publishTimeout must be less than processing.acquireTimeout";
      static final String ACQUIRE_TIMEOUT_MUST_BE_LESS_THAN_CLAIM =
        "processing.acquireTimeout must be less than processing.claimTimeout";
      static final String PROCESSING_WINDOW_MUST_BE_LESS_THAN_CLAIM =
        "processing.claimTimeout must exceed processing.acquireTimeout + processing.publishTimeout";

      public Processing {
        checkArgument(batchSizePerShard > 0, BATCH_SIZE_PER_SHARD_MUST_BE_POSITIVE);
        checkArgument(concurrencyPerShard > 0, CONCURRENCY_PER_SHARD_MUST_BE_POSITIVE);
        checkArgument(batchSizePerShard >= concurrencyPerShard, BATCH_SIZE_MUST_BE_AT_LEAST_CONCURRENCY);
        checkArgument(acquireTimeout.isPositive(), ACQUIRE_TIMEOUT_MUST_BE_POSITIVE);
        checkArgument(publishTimeout.isPositive(), PUBLISH_TIMEOUT_MUST_BE_POSITIVE);
        checkArgument(claimTimeout.isPositive(), CLAIM_TIMEOUT_MUST_BE_POSITIVE);
        checkArgument(publishTimeout.compareTo(acquireTimeout) < 0, PUBLISH_TIMEOUT_MUST_BE_LESS_THAN_ACQUIRE);
        checkArgument(acquireTimeout.compareTo(claimTimeout) < 0, ACQUIRE_TIMEOUT_MUST_BE_LESS_THAN_CLAIM);
        checkArgument(acquireTimeout.plus(publishTimeout).compareTo(claimTimeout) < 0, PROCESSING_WINDOW_MUST_BE_LESS_THAN_CLAIM);
      }
    }

    public record Retry(int maxRetries) {

      static final String MAX_RETRIES_MUST_BE_POSITIVE = "retry.maxRetries must be greater than zero";

      public Retry {
        checkArgument(maxRetries > 0, MAX_RETRIES_MUST_BE_POSITIVE);
      }
    }
  }
}
