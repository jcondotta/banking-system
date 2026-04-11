package com.jcondotta.banking.accounts.outbox.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jcondotta.banking.accounts.infrastructure.support.Preconditions.checkArgument;

@ConfigurationProperties(prefix = "app.outbox.shards")
public record OutboxShardsProperties(int count, int concurrencyPerShard, int batchSizePerShard) {

  static final String COUNT_MUST_BE_POSITIVE = "shards.count must be greater than zero";
  static final String CONCURRENCY_PER_SHARD_MUST_BE_POSITIVE = "shards.concurrencyPerShard must be greater than zero";
  static final String BATCH_SIZE_PER_SHARD_MUST_BE_POSITIVE = "shards.batchSizePerShard must be greater than zero";
  static final String BATCH_SIZE_MUST_BE_AT_LEAST_CONCURRENCY = "shards.batchSizePerShard must be >= shards.concurrencyPerShard";

  public OutboxShardsProperties {
    checkArgument(count > 0, COUNT_MUST_BE_POSITIVE);
    checkArgument(concurrencyPerShard > 0, CONCURRENCY_PER_SHARD_MUST_BE_POSITIVE);
    checkArgument(batchSizePerShard > 0, BATCH_SIZE_PER_SHARD_MUST_BE_POSITIVE);

    checkArgument(batchSizePerShard >= concurrencyPerShard, BATCH_SIZE_MUST_BE_AT_LEAST_CONCURRENCY);
  }

  public Set<Integer> shardIds() {
    return IntStream.range(0, count)
      .boxed()
      .collect(Collectors.toUnmodifiableSet());
  }
}
