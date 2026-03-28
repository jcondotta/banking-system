package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news.ShardNotFoundException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news.ShardTimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxShardExecutorTest {

  private static final int MAX_CONCURRENCY_ALLOWED = 2;
  private static final int NUMBER_OF_SHARDS = 4;

  private static final int SHARD_ZERO = 0;
  private static final int SHARD_ONE = 1;

  private static final Duration ONE_HUNDRED_MILLIS_TIMEOUT = Duration.ofMillis(100);

  private final OutboxShardExecutor executor =
    new OutboxShardExecutor(NUMBER_OF_SHARDS, MAX_CONCURRENCY_ALLOWED);

  @Test
  void shouldExecuteTask_whenShardIsValid() {
    var counter = new AtomicInteger(0);
    executor.execute(SHARD_ZERO, counter::incrementAndGet);

    assertThat(counter.get()).isEqualTo(1);
  }

  @Test
  void shouldReturnValue_whenExecutingSupplier() {
    var counter = new AtomicInteger(0);
    var result = executor.execute(SHARD_ZERO, () -> counter.addAndGet(10));

    assertThat(result).isEqualTo(10);
  }

  @Test
  void shouldExecuteTask_whenPermitAcquiredIsWithinCustomTimeout() {
    var counter = new AtomicInteger(0);
    var result = executor.executeWithTimeout(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, () -> counter.addAndGet(10));

    assertThat(result).isEqualTo(10);
  }

  @Test
  void shouldThrowTimeoutAndRecoverPermits_whenSaturationIsReleased() throws InterruptedException {
    var releaseSignal = saturatePermits(SHARD_ZERO);
    assertThat(executor.getAvailablePermits(SHARD_ZERO)).isZero();

    var counter = new AtomicInteger(0);

    try {
      assertThatThrownBy(() -> executor.executeWithTimeout(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
        .isInstanceOf(ShardTimeoutException.class)
        .hasMessage(ShardTimeoutException.ERROR_MESSAGE.formatted(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT.toMillis()));

      assertThat(counter).hasValue(0);
    }
    finally {
      releaseSignal.countDown();
    }

    executor.executeWithTimeout(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet);
    assertThat(counter).hasValue(1);
  }

  @Test
  void shouldAllowExecutionOnDifferentShard_whenOneShardIsSaturated() throws InterruptedException {
    var releaseSignal = saturatePermits(SHARD_ZERO);
    assertThat(executor.getAvailablePermits(SHARD_ZERO)).isZero();

    try {
      var counter = new AtomicInteger(0);
      executor.execute(SHARD_ONE, counter::incrementAndGet);

      assertThat(counter).hasValue(1);
    }
    finally {
      releaseSignal.countDown();
    }
  }

  @Test
  void shouldBlockIndependently_whenMultipleShardsAreSaturated() throws InterruptedException {
    var releaseShardZero = saturatePermits(SHARD_ZERO);
    var releaseShardOne = saturatePermits(SHARD_ONE);

    try {
      var counter = new AtomicInteger(0);
      assertThatThrownBy(() -> executor.executeWithTimeout(SHARD_ZERO, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
        .isInstanceOf(ShardTimeoutException.class);

      assertThatThrownBy(() -> executor.executeWithTimeout(SHARD_ONE, ONE_HUNDRED_MILLIS_TIMEOUT, counter::incrementAndGet))
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

  @ParameterizedTest
  @ValueSource(ints = {-1, 99})
  void shouldThrowShardNotFoundException_whenShardDoesNotExist(int shard) {
    var counter = new AtomicInteger(0);

    assertThatThrownBy(() -> executor.execute(shard, counter::incrementAndGet))
      .isInstanceOf(ShardNotFoundException.class)
      .hasMessage(ShardNotFoundException.ERROR_MESSAGE.formatted(shard));

    assertThat(counter).hasValue(0);
  }

  private CountDownLatch saturatePermits(int shard) throws InterruptedException {
    final var allPermitsAcquired = new CountDownLatch(MAX_CONCURRENCY_ALLOWED);
    final var releaseSignal = new CountDownLatch(1);

    for (int i = 0; i < MAX_CONCURRENCY_ALLOWED; i++) {
      Thread.ofVirtual().start(() -> executor.execute(shard, () -> {
        allPermitsAcquired.countDown();
        waitUntilReleased(releaseSignal);
      }));
    }
    allPermitsAcquired.await();
    return releaseSignal;
  }

  private void waitUntilReleased(CountDownLatch latch) {
    try {
      latch.await();
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}