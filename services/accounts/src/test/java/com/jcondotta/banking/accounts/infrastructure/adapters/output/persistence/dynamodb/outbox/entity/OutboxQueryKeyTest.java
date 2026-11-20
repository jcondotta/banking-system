package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.store.OutboxQueryKey;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxQueryKeyTest {

  private static final int SHARD = 2;
  private static final Instant NEXT_ATTEMPT_AT = TimeTestFactory.FIXED_INSTANT;

  @Test
  void shouldCreateKey_whenShardAndNextAttemptAtAreValid() {
    var key = OutboxQueryKey.of(SHARD, NEXT_ATTEMPT_AT);

    assertThat(key.partitionKey()).isEqualTo(OutboxQueryKey.PARTITION_KEY_TEMPLATE.formatted(SHARD));
    assertThat(key.sortKey()).isEqualTo(NEXT_ATTEMPT_AT.toString());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -10, Integer.MIN_VALUE})
  void shouldThrowException_whenShardIsNegative(int invalidShard) {
    assertThatThrownBy(() -> OutboxQueryKey.of(invalidShard, NEXT_ATTEMPT_AT))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(OutboxQueryKey.SHARD_REQUIRED);
  }

  @Test
  void shouldThrowException_whenNextAttemptAtIsNull() {
    assertThatThrownBy(() -> OutboxQueryKey.of(SHARD, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(OutboxQueryKey.NEXT_ATTEMPT_AT_REQUIRED);
  }
}