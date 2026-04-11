package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency;

import java.time.Duration;
import java.util.function.Supplier;

public interface ShardExecutor<K> {

  <T> T execute(K shard, Supplier<T> task);

  <T> T execute(K shard, Duration timeout, Supplier<T> task);

  default void execute(K shard, Runnable task) {
    execute(shard, () -> {
      task.run();
      return null;
    });
  }

  default void execute(K shard, Duration timeout, Runnable task) {
    execute(shard, timeout, () -> {
      task.run();
      return null;
    });
  }
}
