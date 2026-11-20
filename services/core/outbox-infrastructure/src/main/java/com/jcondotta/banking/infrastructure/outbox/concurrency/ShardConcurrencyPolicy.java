package com.jcondotta.banking.infrastructure.outbox.concurrency;

import java.time.Duration;
import java.util.function.Supplier;

public interface ShardConcurrencyPolicy<K> {

  <T> T execute(K shard, Duration timeout, Supplier<T> task);
}
