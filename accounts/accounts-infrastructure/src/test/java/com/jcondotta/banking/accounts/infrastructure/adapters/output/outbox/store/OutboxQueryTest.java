package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxQueryTest {

  private static final int DEFAULT_SHARD = 0;
  private static final int DEFAULT_LIMIT = 10;

  @ParameterizedTest
  @EnumSource(OutboxStatus.class)
  void shouldCreateOutboxQuery_whenAllFieldsAreValid(OutboxStatus status) {
    var query = new OutboxQuery(DEFAULT_SHARD, DEFAULT_LIMIT, status);

    assertThat(query.shard()).isEqualTo(DEFAULT_SHARD);
    assertThat(query.limit()).isEqualTo(DEFAULT_LIMIT);
    assertThat(query.status()).isEqualTo(status);
  }

  @Test
  void shouldThrowNullPointerException_whenStatusIsNull() {
    assertThatThrownBy(() -> new OutboxQuery(DEFAULT_SHARD, DEFAULT_LIMIT, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxQuery.STATUS_REQUIRED);
  }
}