package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency;

import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.SemaphoreConcurrencyPolicy;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardExecutionException;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardNotFoundException;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardTimeoutException;
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

class SemaphoreConcurrencyPolicyTest {

  private static final int MAX_CONCURRENCY_ALLOWED = 2;
  private static final Set<Integer> SHARDS = Set.of(0, 1, 2, 3);

  private static final Integer SHARD_ZERO = 0;
  private static final Integer SHARD_ONE = 1;

  private static final Duration ONE_HUNDRED_MILLIS_TIMEOUT = Duration.ofMillis(100);

  private final SemaphoreConcurrencyPolicy<Integer> policy =
    new SemaphoreConcurrencyPolicy<>(SHARDS, MAX_CONCURRENCY_ALLOWED);

  @Test
  void shouldReturnValue_whenExecutingSupplier() {
    var counter = new AtomicInteger(0);

    assertThat(policy.getAvailablePermits(SHARD_ZERO)).isEqualTo(MAX_CONCURRENCY_ALLOWED);
    assertThat(policy.isAtCapacity(SHARD_ZERO)).isFalse();
    assertThat(policy.hasWaitingThreads(SHARD_ZERO)).isFalse();

    var result = policy.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, () -> {
      assertThat(policy.getAvailablePermits(SHARD_ZERO)).isEqualTo(MAX_CONCURRENCY_ALLOWED - 1);
      assertThat(policy.isAtCapacity(SHARD_ZERO)).isFalse();
      assertThat(policy.hasWaitingThreads(SHARD_ZERO)).isFalse();

      return counter.addAndGet(10);
    });

    assertThat(result).isEqualTo(10);

    assertThat(policy.getAvailablePermits(SHARD_ZERO)).isEqualTo(MAX_CONCURRENCY_ALLOWED);
    assertThat(policy.isAtCapacity(SHARD_ZERO)).isFalse();
    assertThat(policy.hasWaitingThreads(SHARD_ZERO)).isFalse();
  }

  @Test
  void shouldThrowTimeoutAndRecoverPermits_whenSaturationIsReleased() {
    var shardReleaser = holdAllSemaphorePermits(SHARD_ZERO);
    assertThat(policy.getAvailablePermits(SHARD_ZERO)).isZero();
    assertThat(policy.isAtCapacity(SHARD_ZERO)).isTrue();

    var counter = new AtomicInteger(0);

    try {
      assertThatThrownBy(() -> policy.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
        .isInstanceOf(ShardTimeoutException.class)
        .hasMessage(ShardTimeoutException.ERROR_MESSAGE.formatted(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT.toMillis()));

      assertThat(counter).hasValue(0);
    }
    finally {
      shardReleaser.countDown();
    }

    policy.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet);
    assertThat(counter).hasValue(1);
  }

  @Test
  void shouldAllowExecutionOnDifferentShard_whenOneShardIsSaturated() {
    var shardReleaser = holdAllSemaphorePermits(SHARD_ZERO);
    assertThat(policy.getAvailablePermits(SHARD_ZERO)).isZero();
    assertThat(policy.isAtCapacity(SHARD_ZERO)).isTrue();

    try {
      var counter = new AtomicInteger(0);
      var result = policy.execute(SHARD_ONE, ONE_HUNDRED_MILLIS_TIMEOUT, () -> counter.addAndGet(10));

      assertThat(result).isEqualTo(10);
    }
    finally {
      shardReleaser.countDown();
    }
  }

  @Test
  void shouldBlockIndependently_whenMultipleShardsAreSaturated() {
    var releaseShardZero = holdAllSemaphorePermits(SHARD_ZERO);
    var releaseShardOne = holdAllSemaphorePermits(SHARD_ONE);

    try {
      var counter = new AtomicInteger(0);

      assertThatThrownBy(() -> policy.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
        .isInstanceOf(ShardTimeoutException.class);

      assertThatThrownBy(() -> policy.execute(SHARD_ONE, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
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
      assertThatThrownBy(() -> policy.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, () -> {
        throw new RuntimeException("exception thrown");
      }));
    }

    var counter = new AtomicInteger(0);
    for (int i = 0; i < MAX_CONCURRENCY_ALLOWED; i++) {
      policy.execute(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet);
    }

    assertThat(counter).hasValue(MAX_CONCURRENCY_ALLOWED);
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, 99})
  void shouldThrowShardNotFoundException_whenShardDoesNotExist(int shard) {
    var counter = new AtomicInteger(0);

    assertThatThrownBy(() -> policy.execute(shard, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
      .isInstanceOf(ShardNotFoundException.class)
      .hasMessage(ShardNotFoundException.ERROR_MESSAGE.formatted(shard));

    assertThat(counter).hasValue(0);
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
        policy.execute(SHARD_ZERO, Duration.ofSeconds(10), () -> null);
      }
      catch (ShardExecutionException e) {
        capturedException.set(e);
        threadInterruptedFlag.set(Thread.currentThread().isInterrupted());
      }
    });

    awaitLatch(threadStarted);

    await().atMost(Duration.ofSeconds(1))
      .until(() -> policy.hasWaitingThreads(SHARD_ZERO));

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

  private CountDownLatch holdAllSemaphorePermits(Integer shard) {
    final var allPermitsAcquired = new CountDownLatch(MAX_CONCURRENCY_ALLOWED);
    final var permitReleaser = new CountDownLatch(1);

    for (int i = 0; i < MAX_CONCURRENCY_ALLOWED; i++) {
      Thread.ofVirtual().start(() -> policy.execute(shard, Duration.ofSeconds(10), () -> {
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
}