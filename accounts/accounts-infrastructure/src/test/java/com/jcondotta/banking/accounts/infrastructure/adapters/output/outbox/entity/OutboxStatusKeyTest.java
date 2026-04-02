package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import com.jcondotta.banking.accounts.infrastructure.arguments_provider.BlankValuesArgumentProvider;
import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxStatusKeyTest {

  private static final String AGGREGATE_ID = "a3b8f9c1-4e2d-4a7b-9f3e-1c2d5e6f7a8b";
  private static final Instant CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @Test
  void shouldCreatePendingKey_whenAggregateIdAndCreatedAtAreValid() {
    var key = OutboxStatusKey.pending(AGGREGATE_ID, CREATED_AT);

    var expectedShard = Math.abs(AGGREGATE_ID.hashCode()) % OutboxConstants.OUTBOX_TOTAL_SHARDS;

    assertThat(key.partitionKey()).isEqualTo(OutboxStatusKey.PARTITION_KEY_TEMPLATE.formatted(expectedShard));
    assertThat(key.sortKey()).isEqualTo(OutboxStatusKey.SORT_KEY_PENDING_TEMPLATE.formatted(CREATED_AT));
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenPendingAggregateIdIsBlank(String invalidAggregateId) {
    assertThatThrownBy(() -> OutboxStatusKey.pending(invalidAggregateId, CREATED_AT))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxStatusKey.AGGREGATE_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenPendingCreatedAtIsNull() {
    assertThatThrownBy(() -> OutboxStatusKey.pending(AGGREGATE_ID, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxStatusKey.CREATED_AT_REQUIRED);
  }

  @Test
  void shouldBuildPartitionKey_whenAggregateIdIsValid() {
    var partitionKey = OutboxStatusKey.buildPartitionKey(AGGREGATE_ID);

    var expectedShard = Math.abs(AGGREGATE_ID.hashCode()) % OutboxConstants.OUTBOX_TOTAL_SHARDS;
    assertThat(partitionKey).isEqualTo(OutboxStatusKey.PARTITION_KEY_TEMPLATE.formatted(expectedShard));
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenBuildPartitionKeyAggregateIdIsBlank(String invalidAggregateId) {
    assertThatThrownBy(() -> OutboxStatusKey.buildPartitionKey(invalidAggregateId))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxStatusKey.AGGREGATE_ID_REQUIRED);
  }

  @ParameterizedTest
  @EnumSource(OutboxStatus.class)
  void shouldBuildSortKey_whenStatusIsAnyValidValue(OutboxStatus status) {
    var sortKey = OutboxStatusKey.buildSortKey(status, CREATED_AT);

    var expectedTemplate = switch (status) {
      case PENDING -> OutboxStatusKey.SORT_KEY_PENDING_TEMPLATE;
      case PROCESSING -> OutboxStatusKey.SORT_KEY_PROCESSING_TEMPLATE;
      case PUBLISHED -> OutboxStatusKey.SORT_KEY_PUBLISHED_TEMPLATE;
      case FAILED -> OutboxStatusKey.SORT_KEY_FAILED_TEMPLATE;
    };

    assertThat(sortKey).isEqualTo(expectedTemplate.formatted(CREATED_AT));
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenPartitionKeyIsBlank(String invalidPartitionKey) {
    assertThatThrownBy(() -> new OutboxStatusKey(invalidPartitionKey, "PENDING#2025-01-01T00:00:00Z"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxStatusKey.PARTITION_KEY_REQUIRED);
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenSortKeyIsBlank(String invalidSortKey) {
    assertThatThrownBy(() -> new OutboxStatusKey("OUTBOX#0", invalidSortKey))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxStatusKey.SORT_KEY_REQUIRED);
  }
}