package com.jcondotta.banking.accounts.infrastructure.properties;

import jakarta.validation.constraints.Min;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record ShardsProperties(

  @Min(1) int count,
  @Min(1) int concurrencyPerShard,
  @Min(1) int batchSizePerShard

) {

  static final String COUNT_MUST_BE_AT_LEAST_ONE = "shards.count must be >= 1";
  static final String CONCURRENCY_PER_SHARD_MUST_BE_AT_LEAST_ONE = "shards.concurrencyPerShard must be >= 1";
  static final String BATCH_SIZE_PER_SHARD_MUST_BE_AT_LEAST_ONE = "shards.batchSizePerShard must be >= 1";
  static final String BATCH_SIZE_MUST_BE_AT_LEAST_CONCURRENCY = "shards.batchSizePerShard must be >= shards.concurrencyPerShard";

  public ShardsProperties {
    if (count < 1) {
      throw new IllegalArgumentException(COUNT_MUST_BE_AT_LEAST_ONE);
    }
    if (concurrencyPerShard < 1) {
      throw new IllegalArgumentException(CONCURRENCY_PER_SHARD_MUST_BE_AT_LEAST_ONE);
    }
    if (batchSizePerShard < 1) {
      throw new IllegalArgumentException(BATCH_SIZE_PER_SHARD_MUST_BE_AT_LEAST_ONE);
    }
    if (batchSizePerShard < concurrencyPerShard) {
      throw new IllegalArgumentException(BATCH_SIZE_MUST_BE_AT_LEAST_CONCURRENCY);
    }
  }

  public Set<Integer> shardIds() {
    return IntStream.range(0, count)
      .boxed()
      .collect(Collectors.toUnmodifiableSet());
  }
}
