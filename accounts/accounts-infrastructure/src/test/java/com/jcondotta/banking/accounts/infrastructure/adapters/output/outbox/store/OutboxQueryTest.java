package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxQueryTest {

  private static final int VALID_SHARD = 0;
  private static final int VALID_LIMIT = 10;

  @Test
  void shouldCreateOutboxQuery_whenShardAndLimitAreValid() {
    var query = OutboxQuery.of(VALID_SHARD, VALID_LIMIT);

    assertThat(query.shard()).isEqualTo(VALID_SHARD);
    assertThat(query.limit()).isEqualTo(VALID_LIMIT);
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -10})
  void shouldThrowException_whenShardIsNegative(int shard) {
    assertThatThrownBy(() -> OutboxQuery.of(shard, VALID_LIMIT))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxQuery.SHARD_MUST_BE_POSITIVE);
  }

  @Test
  void shouldThrowException_whenLimitIsZero() {
    assertThatThrownBy(() -> OutboxQuery.of(VALID_SHARD, 0))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxQuery.LIMIT_MUST_BE_POSITIVE);
  }

  @Test
  void shouldThrowException_whenLimitIsNegative() {
    assertThatThrownBy(() -> OutboxQuery.of(VALID_SHARD, -1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxQuery.LIMIT_MUST_BE_POSITIVE);
  }
}