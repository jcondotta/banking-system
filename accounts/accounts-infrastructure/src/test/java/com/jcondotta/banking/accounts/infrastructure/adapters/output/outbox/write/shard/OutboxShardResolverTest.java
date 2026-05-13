package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.shard;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.shard.ShardCalculator;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxShardsProperties;
import com.jcondotta.domain.identity.AggregateId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxShardResolverTest {

  private static final AggregateId<UUID> AGGREGATE_ID =
    () -> UUID.fromString("6a3a7a45-21ee-4110-9d9a-b619fccd88a6");

  @Test
  void shouldResolveShardUsingConfiguredShardCount() {
    var shardCount = 4;
    var resolver = new OutboxShardResolver(new OutboxShardsProperties(shardCount));

    var shard = resolver.resolve(AGGREGATE_ID);

    assertThat(shard).isEqualTo(ShardCalculator.calculate(AGGREGATE_ID, shardCount));
    assertThat(shard).isBetween(0, shardCount - 1);
  }
}
