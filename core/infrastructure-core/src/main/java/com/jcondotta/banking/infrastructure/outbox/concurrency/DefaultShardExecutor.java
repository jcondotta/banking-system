package com.jcondotta.banking.infrastructure.outbox.concurrency;

import java.time.Duration;
import java.util.function.Supplier;

public class DefaultShardExecutor<K> implements ShardExecutor<K> {

  public static final String ERROR_POLICY_REQUIRED = "policy must be provided";
  public static final String ERROR_TIMEOUT_REQUIRED = "acquireTimeout must be provided";
  public static final String ERROR_TIMEOUT_MUST_BE_POSITIVE = "acquireTimeout must be greater than zero";
  public static final String ERROR_DEFAULT_TIMEOUT_REQUIRED = "defaultTimeout must be provided";
  public static final String ERROR_DEFAULT_TIMEOUT_MUST_BE_POSITIVE = "defaultTimeout must be greater than zero";

  private final ShardConcurrencyPolicy<K> policy;
  private final Duration defaultTimeout;

  public DefaultShardExecutor(ShardConcurrencyPolicy<K> policy, Duration timeout) {
    if (policy == null) {
      throw new IllegalArgumentException(ERROR_POLICY_REQUIRED);
    }

    this.policy = policy;
    this.defaultTimeout = validateTimeout(
      timeout,
      ERROR_DEFAULT_TIMEOUT_REQUIRED,
      ERROR_DEFAULT_TIMEOUT_MUST_BE_POSITIVE
    );
  }

  @Override
  public <T> T execute(K shard, Supplier<T> task) {
    return execute(shard, defaultTimeout, task);
  }

  @Override
  public <T> T execute(K shard, Duration timeout, Supplier<T> task) {
    var validatedTimeout = validateTimeout(timeout, ERROR_TIMEOUT_REQUIRED, ERROR_TIMEOUT_MUST_BE_POSITIVE);

    return policy.execute(shard, validatedTimeout, task);
  }

  private static Duration validateTimeout(Duration timeout, String requiredMessage, String positiveMessage) {
    if (timeout == null) {
      throw new IllegalArgumentException(requiredMessage);
    }
    if (!timeout.isPositive()) {
      throw new IllegalArgumentException(positiveMessage);
    }
    return timeout;
  }
}
