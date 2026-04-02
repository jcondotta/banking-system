package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import com.jcondotta.banking.accounts.infrastructure.arguments_provider.BlankValuesArgumentProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxKeyTest {

  private static final String AGGREGATE_ID = "6a3a7a45-21ee-4110-9d9a-b619fccd88a6";
  private static final String EVENT_ID = "1091df64-40ea-427b-b570-587372241ce8";

  @Test
  void shouldCreateOutboxKey_whenValidAggregateIdAndEventId() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);

    assertThat(key.partitionKey()).isEqualTo(OutboxKey.PARTITION_KEY_TEMPLATE.formatted(AGGREGATE_ID));
    assertThat(key.sortKey()).isEqualTo(OutboxKey.SORT_KEY_TEMPLATE.formatted(EVENT_ID));
  }

  @Test
  void shouldPartitionKeyStartWithBankAccountPrefix_whenValidAggregateId() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);

    assertThat(key.partitionKey()).startsWith("BANK_ACCOUNT#");
  }

  @Test
  void shouldSortKeyStartWithOutboxPrefix_whenValidEventId() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);

    assertThat(key.sortKey()).startsWith("OUTBOX#");
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowIllegalArgumentException_whenAggregateIdIsBlank(String blankAggregateId) {
    assertThatThrownBy(() -> OutboxKey.of(blankAggregateId, EVENT_ID))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxKey.AGGREGATE_ID_REQUIRED);
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowIllegalArgumentException_whenEventIdIsBlank(String blankEventId) {
    assertThatThrownBy(() -> OutboxKey.of(AGGREGATE_ID, blankEventId))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxKey.EVENT_ID_REQUIRED);
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowIllegalArgumentException_whenPartitionKeyIsBlank(String blankPartitionKey) {
    assertThatThrownBy(() -> new OutboxKey(blankPartitionKey, OutboxKey.SORT_KEY_TEMPLATE.formatted(EVENT_ID)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxKey.PARTITION_KEY_REQUIRED);
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowIllegalArgumentException_whenSortKeyIsBlank(String blankSortKey) {
    assertThatThrownBy(() -> new OutboxKey(OutboxKey.PARTITION_KEY_TEMPLATE.formatted(AGGREGATE_ID), blankSortKey))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxKey.SORT_KEY_REQUIRED);
  }
}