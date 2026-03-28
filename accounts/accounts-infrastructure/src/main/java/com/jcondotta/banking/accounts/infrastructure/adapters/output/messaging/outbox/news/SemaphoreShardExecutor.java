package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class SemaphoreShardExecutor<K> implements ShardExecutor<K> {

  public static Duration DEFAULT_TIMEOUT = Duration.ofMillis(200);

  private final Map<K, Semaphore> shardSemaphores;
  private final Duration defaultTimeout;

  public SemaphoreShardExecutor(Set<K> shards, int concurrencyPerShard, Duration defaultTimeout) {
    this.shardSemaphores = new ConcurrentHashMap<>();
    shards.forEach(shard ->
      shardSemaphores.put(shard, new Semaphore(concurrencyPerShard, true))
    );
    this.defaultTimeout = defaultTimeout;
  }

  public SemaphoreShardExecutor(Set<K> shards, int concurrencyPerShard) {
    this(shards, concurrencyPerShard, DEFAULT_TIMEOUT);
  }

  @Override
  public <T> T execute(K shard, Supplier<T> task) {
    return execute(shard, defaultTimeout, task);
  }

  @Override
  public <T> T execute(K shard, Duration timeout, Supplier<T> task) {
    var semaphore = getSemaphore(shard);
    boolean acquired;

    try {
      acquired = semaphore.tryAcquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
      log.info("[shard={}] acquired permit - queued {}", shard, semaphore.getQueueLength());
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ShardExecutionException(shard, e);
    }

    if (!acquired) {
      throw new ShardTimeoutException(shard, timeout.toMillis());
    }

    try {
      return task.get();
    }
    finally {
      log.info("[shard={}] releasing permit - available: {} - queued {}", shard, semaphore.availablePermits(), semaphore.getQueueLength());
      semaphore.release();
    }
  }

  public int getAvailablePermits(K shard) {
    return getSemaphore(shard).availablePermits();
  }

  public boolean isAtCapacity(K shard) {
    return getSemaphore(shard).availablePermits() == 0;
  }

  public boolean hasWaitingThreads(K shard) {
    return getSemaphore(shard).hasQueuedThreads();
  }

  private Semaphore getSemaphore(K shard) {
    var semaphore = shardSemaphores.get(shard);
    if (semaphore == null) {
      throw new ShardNotFoundException(shard);
    }
    return semaphore;
  }
}