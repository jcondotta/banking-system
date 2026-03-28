package com.jcondotta.banking.accounts.infrastructure.properties;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxPropertiesTest {

  private static final int SHARD_COUNT = 4;
  private static final int CONCURRENCY_PER_SHARD = 2;
  private static final int BATCH_SIZE_PER_SHARD = 50;
  private static final Duration PROCESSING_TIMEOUT = Duration.ofSeconds(10);
  private static final Duration POLLING_INTERVAL = Duration.ofSeconds(2);

  @Test
  void shouldBindAllProperties_whenValidConfiguration() {
    var shards = new ShardsProperties(SHARD_COUNT, CONCURRENCY_PER_SHARD, BATCH_SIZE_PER_SHARD);
    var processing = new ProcessingProperties(PROCESSING_TIMEOUT);
    var polling = new PollingProperties(POLLING_INTERVAL);

    var outbox = new OutboxProperties(shards, processing, polling);

    assertThat(outbox.shards().count()).isEqualTo(SHARD_COUNT);
    assertThat(outbox.shards().concurrencyPerShard()).isEqualTo(CONCURRENCY_PER_SHARD);
    assertThat(outbox.shards().batchSizePerShard()).isEqualTo(BATCH_SIZE_PER_SHARD);
    assertThat(outbox.processing().timeout()).isEqualTo(PROCESSING_TIMEOUT);
    assertThat(outbox.polling().interval()).isEqualTo(POLLING_INTERVAL);
  }

  @Test
  void shouldDelegateShardIdsToShardsProperties_whenValidConfiguration() {
    var shards = new ShardsProperties(SHARD_COUNT, CONCURRENCY_PER_SHARD, BATCH_SIZE_PER_SHARD);
    var processing = new ProcessingProperties(PROCESSING_TIMEOUT);
    var polling = new PollingProperties(POLLING_INTERVAL);

    var outbox = new OutboxProperties(shards, processing, polling);

    assertThat(outbox.shards().shardIds())
      .hasSize(SHARD_COUNT)
      .containsExactlyInAnyOrder(0, 1, 2, 3);
  }
}
