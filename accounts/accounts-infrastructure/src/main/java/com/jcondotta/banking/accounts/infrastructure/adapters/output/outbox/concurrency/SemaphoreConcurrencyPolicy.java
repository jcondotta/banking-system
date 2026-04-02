package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardExecutionException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardNotFoundException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardTimeoutException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class SemaphoreConcurrencyPolicy<K> implements ConcurrencyPolicy<K> {

  private final Map<K, Semaphore> shardSemaphores;

  public SemaphoreConcurrencyPolicy(Set<K> shards, int concurrencyPerShard) {
    shardSemaphores = new ConcurrentHashMap<>();
    shards.forEach(shard ->
      shardSemaphores.put(shard, new Semaphore(concurrencyPerShard, true))
    );
  }

  @Override
  public <T> T execute(K shard, Duration timeout, Supplier<T> task) {
    var semaphore = getSemaphore(shard);
    boolean acquired;

    try {
      acquired = semaphore.tryAcquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ShardExecutionException(shard, e);
    }

    if (!acquired) {
      log.warn("[shard={}] timeout acquiring permit after {}ms", shard, timeout.toMillis());
      throw new ShardTimeoutException(shard, timeout.toMillis());
    }

    try {
      return task.get();
    }
    finally {
      semaphore.release();
    }
  }

  public boolean hasWaitingThreads(K shard) {
    return getSemaphore(shard).hasQueuedThreads();
  }

  public int getAvailablePermits(K shard) {
    return getSemaphore(shard).availablePermits();
  }

  public boolean isAtCapacity(K shard) {
    return getSemaphore(shard).availablePermits() == 0;
  }

  private Semaphore getSemaphore(K shard) {
    var semaphore = shardSemaphores.get(shard);
    if (semaphore == null) {
      throw new ShardNotFoundException(shard);
    }
    return semaphore;
  }
}