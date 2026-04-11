package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.shard;

import com.jcondotta.domain.identity.AggregateId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ShardCalculatorTest {

    private static AggregateId<String> aggregateId(String value) {
        return () -> value;
    }

    @Test
    void shouldReturnSameShard_whenCalledMultipleTimesWithSameInput() {
        var id = aggregateId(UUID.randomUUID().toString());
        int shardCount = 8;

        int first = ShardCalculator.calculate(id, shardCount);
        int second = ShardCalculator.calculate(id, shardCount);
        int third = ShardCalculator.calculate(id, shardCount);

        assertThat(first).isEqualTo(second).isEqualTo(third);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 4, 8, 16, 32})
    void shouldReturnShardWithinValidRange_whenShardCountVaries(int shardCount) {
        var id = aggregateId(UUID.randomUUID().toString());

        int shard = ShardCalculator.calculate(id, shardCount);

        assertThat(shard).isBetween(0, shardCount - 1);
    }

    @Test
    void shouldAlwaysReturnZero_whenShardCountIsOne() {
        int shardCount = 1;

        for (int i = 0; i < 20; i++) {
            var id = aggregateId(UUID.randomUUID().toString());
            assertThat(ShardCalculator.calculate(id, shardCount)).isZero();
        }
    }

    @Test
    void shouldDistributeAcrossShards_whenManyAggregateIdsProvided() {
        int shardCount = 8;
        var shards = new java.util.HashSet<Integer>();

        for (int i = 0; i < 200; i++) {
            var id = aggregateId(UUID.randomUUID().toString());
            shards.add(ShardCalculator.calculate(id, shardCount));
        }

        assertThat(shards).hasSize(shardCount);
    }

    @Test
    void shouldReturnZero_whenHashCodeIsIntegerMinValue() {
        // "polygenelubricants".hashCode() == Integer.MIN_VALUE
        // Integer.MIN_VALUE & Integer.MAX_VALUE == 0, so shard must be 0
        var negativeHashId = aggregateId("polygenelubricants");

        int shard = ShardCalculator.calculate(negativeHashId, 8);

        assertThat(shard).isZero();
    }
}
