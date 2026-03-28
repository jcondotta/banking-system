package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news;

import java.time.Duration;
import java.util.function.Supplier;

public interface ShardExecutor<K> {

  <T> T execute(K shard, Supplier<T> task);

  <T> T execute(K shard, Duration timeout, Supplier<T> task);
}