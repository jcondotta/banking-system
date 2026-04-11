package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency;

import java.time.Duration;
import java.util.function.Supplier;

public interface ConcurrencyPolicy<K> {

  <T> T execute(K shard, Duration timeout, Supplier<T> task);
}
