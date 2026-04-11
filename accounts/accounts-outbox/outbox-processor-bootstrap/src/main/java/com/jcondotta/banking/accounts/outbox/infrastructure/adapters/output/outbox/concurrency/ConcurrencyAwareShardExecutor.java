package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Supplier;

import static com.jcondotta.banking.accounts.infrastructure.support.Preconditions.required;

@Slf4j
public class ConcurrencyAwareShardExecutor<K> implements ShardExecutor<K> {

  static final String ERROR_POLICY_REQUIRED = "policy must be provided";
  static final String ERROR_TIMEOUT_REQUIRED = "acquireTimeout must be provided";
  static final String ERROR_DEFAULT_TIMEOUT_REQUIRED = "defaultTimeout must be provided";

  private final ConcurrencyPolicy<K> policy;
  private final Duration defaultTimeout;

  public ConcurrencyAwareShardExecutor(ConcurrencyPolicy<K> policy, Duration timeout) {
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

    log.debug("[shard={}] submitting task with acquireTimeout={}ms", shard, timeout.toMillis());

    return policy.execute(shard, timeout, task);
  }
}
