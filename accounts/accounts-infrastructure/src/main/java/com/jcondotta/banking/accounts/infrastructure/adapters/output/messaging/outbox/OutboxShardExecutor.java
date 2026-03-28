package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news.ShardNotFoundException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news.ShardTimeoutException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class OutboxShardExecutor {

  private final Map<Integer, Semaphore> shardSemaphores = new ConcurrentHashMap<>();
  private final Duration defaultTimeout;

  public OutboxShardExecutor(int totalShards, int concurrencyPerShard, Duration defaultTimeout) {
    for (int shard = 0; shard < totalShards; shard++) {
      shardSemaphores.put(shard, new Semaphore(concurrencyPerShard, true));
    }
    this.defaultTimeout = defaultTimeout;
  }

  public OutboxShardExecutor(int totalShards, int concurrencyPerShard) {
    this(totalShards, concurrencyPerShard, Duration.ofSeconds(30));
  }

//  // --- Execute com timeout default ---
//
  public void execute(int shard, Runnable task) {
    execute(shard, () -> {
      task.run();
      return null;
    });
  }

  public <T> T execute(int shard, Supplier<T> task) {
    return executeWithTimeout(shard, defaultTimeout, task);
  }
//
//  public void executeWithTimeout(int shard, Duration timeout, Runnable task) {
//    executeWithTimeout(shard, timeout, () -> {
//      task.run();
//      return null;
//    });
//  }

  public <T> T executeWithTimeout(int shard, Duration timeout, Supplier<T> task) {
    var semaphore = getSemaphore(shard);
    boolean acquired = false;
    try {
      acquired = semaphore.tryAcquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
      log.info("acquiring permit - available: {} - waiting: {}", semaphore.availablePermits(), semaphore.getQueueLength());
      if (!acquired) {
        throw new ShardTimeoutException(shard, timeout.toMillis());
      }
      return task.get();
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Thread interrupted while acquiring semaphore for shard " + shard, e);
    }
    finally {
      if (acquired) {
        log.info("releasing permit - available: {} - waiting: {}", semaphore.availablePermits(), semaphore.getQueueLength());
        semaphore.release();
      }
    }
  }

  public int getAvailablePermits(int shard) {
    return getSemaphore(shard).availablePermits();
  }

  public int getQueuedThreads(int shard) {
    return getSemaphore(shard).getQueueLength();
  }

  public boolean hasQueuedThreads(int shard) {
    return getSemaphore(shard).hasQueuedThreads();
  }

  // --- Internals ---

  private Semaphore getSemaphore(int shard) {
    var semaphore = shardSemaphores.get(shard);
    if (semaphore == null) {
      throw new ShardNotFoundException(shard);
    }
    return semaphore;
  }
}