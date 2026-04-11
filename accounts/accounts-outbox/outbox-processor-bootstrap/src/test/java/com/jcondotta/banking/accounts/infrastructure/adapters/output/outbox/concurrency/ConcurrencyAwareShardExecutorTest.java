package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency;

import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.ConcurrencyAwareShardExecutor;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.ConcurrencyPolicy;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardNotFoundException;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardTimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcurrencyAwareShardExecutorTest {

  private static final Integer SHARD_ZERO = 0;
  private static final Duration DEFAULT_TIMEOUT = Duration.ofMillis(500);
  private static final Duration CUSTOM_TIMEOUT = Duration.ofMillis(100);

  @Mock
  private ConcurrencyPolicy<Integer> policy;

  @Test
  void shouldUseDefaultTimeout_whenNoTimeoutProvided() {
    var executor = new ConcurrencyAwareShardExecutor<>(policy, DEFAULT_TIMEOUT);

    executor.execute(SHARD_ZERO, () -> null);

    verify(policy).execute(eq(SHARD_ZERO), eq(DEFAULT_TIMEOUT), any());
    verifyNoMoreInteractions(policy);
  }

  @Test
  void shouldDelegateToPolicy_whenExecutingWithDefaultTimeout() {
    var executor = new ConcurrencyAwareShardExecutor<>(policy, DEFAULT_TIMEOUT);
    var counter = new AtomicInteger(0);

    executeTaskFromPolicy(SHARD_ZERO, DEFAULT_TIMEOUT);

    var result = executor.execute(SHARD_ZERO, counter::incrementAndGet);

    assertThat(result).isEqualTo(1);

    verify(policy).execute(eq(SHARD_ZERO), eq(DEFAULT_TIMEOUT), any());
    verifyNoMoreInteractions(policy);
  }

  @Test
  void shouldDelegateToPolicy_whenExecutingWithExplicitTimeout() {
    var executor = new ConcurrencyAwareShardExecutor<>(policy, DEFAULT_TIMEOUT);
    var counter = new AtomicInteger(0);

    executeTaskFromPolicy(SHARD_ZERO, CUSTOM_TIMEOUT);

    var result = executor.execute(SHARD_ZERO, CUSTOM_TIMEOUT, counter::incrementAndGet);

    assertThat(result).isEqualTo(1);

    verify(policy).execute(eq(SHARD_ZERO), eq(CUSTOM_TIMEOUT), any());
    verifyNoMoreInteractions(policy);
  }

  @Test
  void shouldThrowIllegalArgumentException_whenPolicyIsNull() {
    assertThatThrownBy(() -> new ConcurrencyAwareShardExecutor<>(null, DEFAULT_TIMEOUT))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(ConcurrencyAwareShardExecutor.ERROR_POLICY_REQUIRED);
  }

  @Test
  void shouldThrowIllegalArgumentException_whenDefaultTimeoutIsNull() {
    assertThatThrownBy(() -> new ConcurrencyAwareShardExecutor<>(policy, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(ConcurrencyAwareShardExecutor.ERROR_DEFAULT_TIMEOUT_REQUIRED);
  }

  @Test
  void shouldThrowIllegalArgumentException_whenExplicitTimeoutIsNull() {
    var executor = new ConcurrencyAwareShardExecutor<>(policy, DEFAULT_TIMEOUT);
    var counter = new AtomicInteger(0);

    assertThatThrownBy(() -> executor.execute(SHARD_ZERO, null, counter::incrementAndGet))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(ConcurrencyAwareShardExecutor.ERROR_TIMEOUT_REQUIRED);

    assertThat(counter.get()).isZero();
    verifyNoInteractions(policy);
  }

  @Test
  void shouldPropagateException_whenPolicyThrowsShardTimeoutException() {
    var executor = new ConcurrencyAwareShardExecutor<>(policy, DEFAULT_TIMEOUT);

    when(policy.execute(eq(SHARD_ZERO), eq(DEFAULT_TIMEOUT), any()))
      .thenThrow(new ShardTimeoutException(SHARD_ZERO, DEFAULT_TIMEOUT.toMillis()));

    assertThatThrownBy(() -> executor.execute(SHARD_ZERO, () -> null))
      .isInstanceOf(ShardTimeoutException.class)
      .hasMessage(ShardTimeoutException.ERROR_MESSAGE.formatted(SHARD_ZERO, DEFAULT_TIMEOUT.toMillis()));
  }

  @Test
  void shouldPropagateException_whenPolicyThrowsShardNotFoundException() {
    var executor = new ConcurrencyAwareShardExecutor<>(policy, DEFAULT_TIMEOUT);

    when(policy.execute(eq(SHARD_ZERO), eq(DEFAULT_TIMEOUT), any()))
      .thenThrow(new ShardNotFoundException(SHARD_ZERO));

    assertThatThrownBy(() -> executor.execute(SHARD_ZERO, () -> null))
      .isInstanceOf(ShardNotFoundException.class)
      .hasMessage(ShardNotFoundException.ERROR_MESSAGE.formatted(SHARD_ZERO));
  }

  @Test
  void shouldPropagateException_whenTaskThrowsException() {
    var executor = new ConcurrencyAwareShardExecutor<>(policy, DEFAULT_TIMEOUT);

    executeTaskFromPolicy(SHARD_ZERO, DEFAULT_TIMEOUT);

    RuntimeException exception = new RuntimeException("exception from task");

    assertThatThrownBy(() ->
      executor.execute(SHARD_ZERO, () -> { throw exception; })
    ).isSameAs(exception);
  }

  @SuppressWarnings("all")
  private void executeTaskFromPolicy(Integer shard, Duration timeout) {
    when(policy.execute(eq(shard), eq(timeout), any()))
      .thenAnswer(invocation -> {
        var task = invocation.getArgument(2, Supplier.class);
        return task.get();
      });
  }
}