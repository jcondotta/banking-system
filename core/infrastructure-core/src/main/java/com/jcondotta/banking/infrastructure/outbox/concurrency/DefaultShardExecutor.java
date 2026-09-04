package com.jcondotta.banking.infrastructure.outbox.concurrency;

import java.time.Duration;
import java.util.function.Supplier;

import static com.jcondotta.domain.support.Preconditions.required;

public class DefaultShardExecutor<K> implements ShardExecutor<K> {

  public static final String ERROR_POLICY_REQUIRED = "policy must be provided";
  public static final String ERROR_TIMEOUT_REQUIRED = "acquireTimeout must be provided";
  public static final String ERROR_DEFAULT_TIMEOUT_REQUIRED = "defaultTimeout must be provided";

  private final ShardConcurrencyPolicy<K> policy;
  private final Duration defaultTimeout;

  public DefaultShardExecutor(ShardConcurrencyPolicy<K> policy, Duration timeout) {
    this.policy = required(policy, ERROR_POLICY_REQUIRED);
    this.defaultTimeout = required(timeout, ERROR_DEFAULT_TIMEOUT_REQUIRED);
  }

  @Override
  public <T> T execute(K shard, Supplier<T> task) {
    return execute(shard, defaultTimeout, task);
  }

  @Override
  public <T> T execute(K shard, Duration timeout, Supplier<T> task) {
    if (timeout == null) {
      throw new IllegalArgumentException(ERROR_TIMEOUT_REQUIRED);
    }

    return policy.execute(shard, timeout, task);
  }
}
