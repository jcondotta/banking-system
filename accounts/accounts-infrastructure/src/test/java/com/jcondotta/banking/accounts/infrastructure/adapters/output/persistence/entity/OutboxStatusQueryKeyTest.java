package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.OutboxStatus;
import com.jcondotta.banking.accounts.infrastructure.arguments_provider.BlankValuesArgumentProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxStatusQueryKeyTest {

  private static final int SHARD = 0;

  @ParameterizedTest
  @EnumSource(OutboxStatus.class)
  void shouldCreateQueryKey_whenShardAndStatusAreValid(OutboxStatus status) {
    var key = OutboxStatusQueryKey.of(SHARD, status);

    assertThat(key.partitionKey()).isEqualTo(OutboxStatusQueryKey.buildPartitionKey(SHARD));
    assertThat(key.sortKey()).isEqualTo(OutboxStatusQueryKey.buildSortKey(status));
  }

  @Test
  void shouldThrowException_whenOfStatusIsNull() {
    assertThatThrownBy(() -> OutboxStatusQueryKey.of(SHARD, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxStatusQueryKey.STATUS_REQUIRED);
  }

  @Test
  void shouldBuildPartitionKey_whenShardIsValid() {
    var partitionKey = OutboxStatusQueryKey.buildPartitionKey(SHARD);

    assertThat(partitionKey).isEqualTo(OutboxStatusQueryKey.PARTITION_KEY_TEMPLATE.formatted(SHARD));
  }

  @Test
  void shouldBuildSortKey_whenStatusIsPending() {
    assertThat(OutboxStatusQueryKey.buildSortKey(OutboxStatus.PENDING))
      .isEqualTo(OutboxStatusQueryKey.SORT_KEY_PENDING_PREFIX);
  }

  @Test
  void shouldBuildSortKey_whenStatusIsPublished() {
    assertThat(OutboxStatusQueryKey.buildSortKey(OutboxStatus.PUBLISHED))
      .isEqualTo(OutboxStatusQueryKey.SORT_KEY_PUBLISHED_PREFIX);
  }

  @Test
  void shouldBuildSortKey_whenStatusIsFailed() {
    assertThat(OutboxStatusQueryKey.buildSortKey(OutboxStatus.FAILED))
      .isEqualTo(OutboxStatusQueryKey.SORT_KEY_FAILED_PREFIX);
  }

  @Test
  void shouldThrowException_whenBuildSortKeyStatusIsNull() {
    assertThatThrownBy(() -> OutboxStatusQueryKey.buildSortKey(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxStatusQueryKey.STATUS_REQUIRED);
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenPartitionKeyIsBlank(String invalidPartitionKey) {
    assertThatThrownBy(() -> new OutboxStatusQueryKey(invalidPartitionKey, OutboxStatusQueryKey.SORT_KEY_PENDING_PREFIX))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxStatusQueryKey.PARTITION_KEY_REQUIRED);
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenSortKeyIsBlank(String invalidSortKey) {
    assertThatThrownBy(() -> new OutboxStatusQueryKey(OutboxStatusQueryKey.buildPartitionKey(SHARD), invalidSortKey))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxStatusQueryKey.SORT_KEY_REQUIRED);
  }
}