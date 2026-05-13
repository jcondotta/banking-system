package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import com.jcondotta.domain.identity.AggregateId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxKeyTest {

  private static final AggregateId<UUID> AGGREGATE_ID = () -> UUID.fromString("6a3a7a45-21ee-4110-9d9a-b619fccd88a6");
  private static final UUID EVENT_ID = UUID.fromString("1091df64-40ea-427b-b570-587372241ce8");

  @Test
  void shouldCreateOutboxKey_whenValidAggregateIdAndEventId() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);

    assertThat(key.partitionKey()).isEqualTo(OutboxKey.PARTITION_KEY_TEMPLATE.formatted(AGGREGATE_ID.asString()));
    assertThat(key.sortKey()).isEqualTo(OutboxKey.SORT_KEY_TEMPLATE.formatted(EVENT_ID.toString()));
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

  @Test
  void shouldThrowIllegalArgumentException_whenAggregateIdIsNull() {
    assertThatThrownBy(() -> OutboxKey.of(null, EVENT_ID))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxKey.AGGREGATE_ID_REQUIRED);
  }

  @Test
  void shouldThrowIllegalArgumentException_whenEventIdIsNull() {
    assertThatThrownBy(() -> OutboxKey.of(AGGREGATE_ID, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxKey.EVENT_ID_REQUIRED);
  }

  @Test
  void shouldBeEqual_whenSameInstance() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);

    assertThat(key).isEqualTo(key);
  }

  @Test
  void shouldBeEqualAndHaveSameHashCode_whenValuesAreEqual() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);
    var sameKey = OutboxKey.of(AGGREGATE_ID, EVENT_ID);

    assertThat(key)
      .isEqualTo(sameKey)
      .hasSameHashCodeAs(sameKey);
  }

  @Test
  void shouldNotBeEqual_whenComparedWithNull() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);

    assertThat(key).isNotEqualTo(null);
  }

  @Test
  void shouldNotBeEqual_whenComparedWithDifferentType() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);

    assertThat(key).isNotEqualTo("outbox-key");
  }

  @Test
  void shouldNotBeEqual_whenPartitionKeyIsDifferent() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);
    var otherAggregateId = (AggregateId<UUID>) () -> UUID.fromString("df90445c-c5ac-4ac4-a285-d1be82d3f7e6");
    var otherKey = OutboxKey.of(otherAggregateId, EVENT_ID);

    assertThat(key).isNotEqualTo(otherKey);
  }

  @Test
  void shouldNotBeEqual_whenSortKeyIsDifferent() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);
    var otherEventId = UUID.fromString("ac688d89-26a4-46e4-ad50-ecbb56cfd508");
    var otherKey = OutboxKey.of(AGGREGATE_ID, otherEventId);

    assertThat(key).isNotEqualTo(otherKey);
  }

  @Test
  void shouldReturnFormattedValue_whenToStringIsCalled() {
    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);

    assertThat(key)
      .hasToString("OutboxKey[partitionKey=BANK_ACCOUNT#%s, sortKey=OUTBOX#%s]"
        .formatted(AGGREGATE_ID.asString(), EVENT_ID));
  }
}
