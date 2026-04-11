package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store;

import static com.jcondotta.banking.accounts.infrastructure.support.Preconditions.checkArgument;

public record OutboxQuery(int shard, int limit) {

  static final String SHARD_MUST_BE_POSITIVE = "shard must be >= 0";
  static final String LIMIT_MUST_BE_POSITIVE = "limit must be >= 1";

  public OutboxQuery {
    checkArgument(shard >= 0, SHARD_MUST_BE_POSITIVE);
    checkArgument(limit >= 1, LIMIT_MUST_BE_POSITIVE);
  }

  public static OutboxQuery of(int shard, int limit) {
    return new OutboxQuery(shard, limit);
  }
}
