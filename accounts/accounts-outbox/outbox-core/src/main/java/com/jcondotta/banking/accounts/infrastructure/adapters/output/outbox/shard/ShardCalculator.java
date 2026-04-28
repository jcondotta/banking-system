package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.shard;

import com.jcondotta.domain.identity.AggregateId;

public final class ShardCalculator {

  private ShardCalculator() {
  }

  public static int calculate(AggregateId<?> aggregateId, int shardCount) {
    return (aggregateId.asString().hashCode() & Integer.MAX_VALUE) % shardCount;
  }
}
