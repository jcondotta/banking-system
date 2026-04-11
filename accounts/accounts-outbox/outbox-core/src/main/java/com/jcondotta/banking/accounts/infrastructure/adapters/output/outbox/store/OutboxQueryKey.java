package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store;

import java.time.Instant;

import static com.jcondotta.banking.accounts.infrastructure.support.Preconditions.checkArgument;
import static com.jcondotta.banking.accounts.infrastructure.support.Preconditions.required;

public record OutboxQueryKey(String partitionKey, String sortKey) {

  public static final String PARTITION_KEY_TEMPLATE = "OUTBOX#%s";

  public static final String SHARD_REQUIRED = "shard must be >= 0";
  public static final String NEXT_ATTEMPT_AT_REQUIRED = "nextAttemptAt must be provided";

  public static OutboxQueryKey of(int shard, Instant nextAttemptAt) {
    checkArgument(shard >= 0, SHARD_REQUIRED);
    required(nextAttemptAt, NEXT_ATTEMPT_AT_REQUIRED);

    var partitionKey = PARTITION_KEY_TEMPLATE.formatted(shard);
    var sortKey = nextAttemptAt.toString();

    return new OutboxQueryKey(partitionKey, sortKey);
  }
}
