package com.jcondotta.banking.infrastructure.outbox.shard;

import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.domain.identity.AggregateId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxShardResolverTest {

    private static final AggregateId<UUID> AGGREGATE_ID =
        () -> UUID.fromString("6a3a7a45-21ee-4110-9d9a-b619fccd88a6");

    private static AggregateId<String> aggregateId(String value) {
        return () -> value;
    }

    private OutboxShardResolver resolverWith(int shardCount) {
        return new OutboxShardResolver(new OutboxProperties.Shards(shardCount));
    }

    @Test
    void shouldResolveShardWithinValidRange() {
        var shardCount = 4;
        var shard = resolverWith(shardCount).resolve(AGGREGATE_ID);

        assertThat(shard).isBetween(0, shardCount - 1);
    }

    @Test
    void shouldReturnSameShard_whenCalledMultipleTimesWithSameInput() {
        var resolver = resolverWith(8);
        var id = aggregateId(UUID.randomUUID().toString());

        assertThat(resolver.resolve(id))
            .isEqualTo(resolver.resolve(id))
            .isEqualTo(resolver.resolve(id));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 4, 8, 16, 32})
    void shouldReturnShardWithinValidRange_whenShardCountVaries(int shardCount) {
        var id = aggregateId(UUID.randomUUID().toString());
        var shard = resolverWith(shardCount).resolve(id);

        assertThat(shard).isBetween(0, shardCount - 1);
    }

    @Test
    void shouldAlwaysReturnZero_whenShardCountIsOne() {
        var resolver = resolverWith(1);

        for (int i = 0; i < 20; i++) {
            assertThat(resolver.resolve(aggregateId(UUID.randomUUID().toString()))).isZero();
        }
    }

    @Test
    void shouldDistributeAcrossAllShards_whenManyAggregateIdsProvided() {
        int shardCount = 8;
        var resolver = resolverWith(shardCount);
        var shards = new HashSet<Integer>();

        for (int i = 0; i < 200; i++) {
            shards.add(resolver.resolve(aggregateId(UUID.randomUUID().toString())));
        }

        assertThat(shards).hasSize(shardCount);
    }

    @Test
    void shouldReturnZero_whenAggregateIdHashCodeIsIntegerMinValue() {
        // "polygenelubricants".hashCode() == Integer.MIN_VALUE
        // Integer.MIN_VALUE & Integer.MAX_VALUE == 0, so shard must always be 0
        var resolver = resolverWith(8);

        assertThat(resolver.resolve(aggregateId("polygenelubricants"))).isZero();
    }
}
