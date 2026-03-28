package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

class SemaphoreShardExecutorTest {

  private static final int MAX_CONCURRENCY_ALLOWED = 2;
  private static final Set<Integer> SHARDS = Set.of(0, 1, 2, 3);

  private static final Integer SHARD_ZERO = 0;
  private static final Integer SHARD_ONE = 1;

  private static final Duration ONE_HUNDRED_MILLIS_TIMEOUT = Duration.ofMillis(100);

  private final SemaphoreShardExecutor<Integer> executor =
    new SemaphoreShardExecutor<>(SHARDS, MAX_CONCURRENCY_ALLOWED);

  @Test
  void shouldReturnValue_whenExecutingSupplier() {
    var counter = new AtomicInteger(0);

    assertThat(executor.getAvailablePermits(SHARD_ZERO)).isEqualTo(MAX_CONCURRENCY_ALLOWED);
    assertThat(executor.isAtCapacity(SHARD_ZERO)).isFalse();
    assertThat(executor.hasWaitingThreads(SHARD_ZERO)).isFalse();

    var result = executor.execute(SHARD_ZERO, () -> {
      assertThat(executor.getAvailablePermits(SHARD_ZERO)).isEqualTo(MAX_CONCURRENCY_ALLOWED - 1);
      assertThat(executor.isAtCapacity(SHARD_ZERO)).isFalse();
      assertThat(executor.hasWaitingThreads(SHARD_ZERO)).isFalse();

      return counter.addAndGet(10);
    });

    assertThat(result).isEqualTo(10);

    assertThat(executor.getAvailablePermits(SHARD_ZERO)).isEqualTo(MAX_CONCURRENCY_ALLOWED);
    assertThat(executor.isAtCapacity(SHARD_ZERO)).isFalse();
    assertThat(executor.hasWaitingThreads(SHARD_ZERO)).isFalse();
  }

  @Test
  void shouldReturnValue_whenExecutingSupplierWithCustomTimeout() {
    var counter = new AtomicInteger(0);
    var result = executor.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, () -> counter.addAndGet(10));

    assertThat(result).isEqualTo(10);
  }

  @Test
  void shouldThrowTimeoutAndRecoverPermits_whenSaturationIsReleased() {
    var shardReleaser = holdAllSemaphorePermits(SHARD_ZERO);
    assertThat(executor.getAvailablePermits(SHARD_ZERO)).isZero();

    var counter = new AtomicInteger(0);

    try {
      assertThatThrownBy(() -> executor.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
        .isInstanceOf(ShardTimeoutException.class)
        .hasMessage(ShardTimeoutException.ERROR_MESSAGE.formatted(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT.toMillis()));

      assertThat(counter).hasValue(0);
    }
    finally {
      shardReleaser.countDown();
    }

    executor.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet);
    assertThat(counter).hasValue(1);
  }

  @Test
  void shouldAllowExecutionOnDifferentShard_whenOneShardIsSaturated() {
    var shardReleaser = holdAllSemaphorePermits(SHARD_ZERO);
    assertThat(executor.getAvailablePermits(SHARD_ZERO)).isZero();

    try {
      var counter = new AtomicInteger(0);
      var result = executor.execute(SHARD_ONE, () -> counter.addAndGet(10));

      assertThat(result).isEqualTo(10);
    } finally {
      shardReleaser.countDown();
    }
  }

  @Test
  void shouldBlockIndependently_whenMultipleShardsAreSaturated() {
    var releaseShardZero = holdAllSemaphorePermits(SHARD_ZERO);
    var releaseShardOne = holdAllSemaphorePermits(SHARD_ONE);

    try {
      var counter = new AtomicInteger(0);

      assertThatThrownBy(() -> executor.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
        .isInstanceOf(ShardTimeoutException.class);

      assertThatThrownBy(() -> executor.execute(SHARD_ONE, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
        .isInstanceOf(ShardTimeoutException.class);

      assertThat(counter).hasValue(0);
    }
    finally {
      releaseShardZero.countDown();
      releaseShardOne.countDown();
    }
  }

  @Test
  void shouldRestoreFullExecutionCapacity_whenMultipleTasksFail() {
    for (int i = 0; i < MAX_CONCURRENCY_ALLOWED * 2; i++) {
      assertThatThrownBy(() -> executor.execute(SHARD_ZERO, () -> {
        throw new RuntimeException("exception thrown");
      }));
    }

    var counter = new AtomicInteger(0);
    for (int i = 0; i < MAX_CONCURRENCY_ALLOWED; i++) {
      executor.execute(SHARD_ZERO, counter::incrementAndGet);
    }

    assertThat(counter).hasValue(MAX_CONCURRENCY_ALLOWED);
  }

  @Test
  void shouldUseDefaultTimeout_whenShardIsSaturatedAndNoTimeoutProvided() {
    final var shardReleaser = holdAllSemaphorePermits(SHARD_ZERO);

    try {
      final var counter = new AtomicInteger(0);

      assertThatThrownBy(() -> executor.execute(SHARD_ZERO, counter::incrementAndGet))
        .isInstanceOf(ShardTimeoutException.class)
        .hasMessage(ShardTimeoutException.ERROR_MESSAGE.formatted(SHARD_ZERO, SemaphoreShardExecutor.DEFAULT_TIMEOUT.toMillis()));

      assertThat(counter).hasValue(0);
    }
    finally {
      shardReleaser.countDown();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 99})
  void shouldThrowShardNotFoundException_whenShardDoesNotExist(int shard) {
    var counter = new AtomicInteger(0);

    assertThatThrownBy(() -> executor.execute(shard, counter::incrementAndGet))
      .isInstanceOf(ShardNotFoundException.class)
      .hasMessage(ShardNotFoundException.ERROR_MESSAGE.formatted(shard));

    assertThat(counter).hasValue(0);
  }

  private CountDownLatch holdAllSemaphorePermits(Integer shard) {
    final var allPermitsAcquired = new CountDownLatch(MAX_CONCURRENCY_ALLOWED);
    final var permitReleaser = new CountDownLatch(1);

    for (int i = 0; i < MAX_CONCURRENCY_ALLOWED; i++) {
      Thread.ofVirtual().start(() -> executor.execute(shard, () -> {
        allPermitsAcquired.countDown();
        awaitLatch(permitReleaser);
        return null;
      }));
    }

    awaitLatch(allPermitsAcquired);

    return permitReleaser;
  }

  private void awaitLatch(CountDownLatch latch) {
    try {
      latch.await();
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Test
  void shouldThrowShardExecutionException_whenInterruptedWhileWaitingForPermit() throws InterruptedException {
    var shardReleaser = holdAllSemaphorePermits(SHARD_ZERO);
    var capturedException = new AtomicReference<Throwable>();
    var threadStarted = new CountDownLatch(1);
    var threadInterruptedFlag = new AtomicReference<Boolean>();

    var thread = Thread.ofVirtual().start(() -> {
      threadStarted.countDown();
      try {
        executor.execute(SHARD_ZERO, Duration.ofSeconds(10), () -> null);
      }
      catch (ShardExecutionException t) {
        capturedException.set(t);
        threadInterruptedFlag.set(Thread.currentThread().isInterrupted());
      }
    });

    awaitLatch(threadStarted);

    await().atMost(Duration.ofSeconds(1))
      .until(() -> executor.hasWaitingThreads(SHARD_ZERO));

    thread.interrupt();
    thread.join();

    try {
      assertThat(capturedException.get())
        .isNotNull()
        .isInstanceOf(ShardExecutionException.class)
        .hasCauseInstanceOf(InterruptedException.class);

      assertThat(threadInterruptedFlag.get())
        .as("interrupt flag should be preserved after interruption")
        .isTrue();
    }
    finally {
      shardReleaser.countDown();
    }
  }
}