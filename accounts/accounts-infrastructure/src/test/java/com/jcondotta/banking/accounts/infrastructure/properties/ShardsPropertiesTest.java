package com.jcondotta.banking.accounts.infrastructure.properties;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShardsPropertiesTest {

  private static final int SHARD_COUNT = 4;
  private static final int CONCURRENCY_PER_SHARD = 2;
  private static final int BATCH_SIZE_PER_SHARD = 50;

  @Test
  void shouldGenerateShardIds_whenCountIsValid() {
    var shards = new ShardsProperties(SHARD_COUNT, CONCURRENCY_PER_SHARD, BATCH_SIZE_PER_SHARD);

    var expectedIds = IntStream.range(0, SHARD_COUNT).boxed().toList();

    assertThat(shards.shardIds())
      .hasSize(SHARD_COUNT)
      .containsExactlyInAnyOrderElementsOf(expectedIds);
  }

  @Test
  void shouldThrowException_whenCountIsLessThanOne() {
    assertThatThrownBy(() -> new ShardsProperties(0, CONCURRENCY_PER_SHARD, BATCH_SIZE_PER_SHARD))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(ShardsProperties.COUNT_MUST_BE_AT_LEAST_ONE);
  }

  @Test
  void shouldThrowException_whenConcurrencyPerShardIsLessThanOne() {
    assertThatThrownBy(() -> new ShardsProperties(SHARD_COUNT, 0, BATCH_SIZE_PER_SHARD))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(ShardsProperties.CONCURRENCY_PER_SHARD_MUST_BE_AT_LEAST_ONE);
  }

  @Test
  void shouldThrowException_whenBatchSizePerShardIsLessThanOne() {
    assertThatThrownBy(() -> new ShardsProperties(SHARD_COUNT, CONCURRENCY_PER_SHARD, 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(ShardsProperties.BATCH_SIZE_PER_SHARD_MUST_BE_AT_LEAST_ONE);
  }

  @Test
  void shouldThrowException_whenBatchSizeIsLessThanConcurrency() {
    int concurrency = 10;
    int batchSizeSmallerThanConcurrency = concurrency - 1;

    assertThatThrownBy(() -> new ShardsProperties(SHARD_COUNT, concurrency, batchSizeSmallerThanConcurrency))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(ShardsProperties.BATCH_SIZE_MUST_BE_AT_LEAST_CONCURRENCY);
  }

  @Test
  void shouldAcceptConfiguration_whenBatchSizeEqualsCountConcurrency() {
    int concurrency = 5;
    int batchSizeEqualToConcurrency = 5;

    var shards = new ShardsProperties(SHARD_COUNT, concurrency, batchSizeEqualToConcurrency);

    assertThat(shards.batchSizePerShard()).isEqualTo(batchSizeEqualToConcurrency);
    assertThat(shards.concurrencyPerShard()).isEqualTo(concurrency);
  }
}
