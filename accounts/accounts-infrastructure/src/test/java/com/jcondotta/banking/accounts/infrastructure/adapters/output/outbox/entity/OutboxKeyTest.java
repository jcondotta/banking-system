package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

class OutboxKeyTest {

//  private static final AggregateId<UUID> AGGREGATE_ID = () -> UUID.fromString("6a3a7a45-21ee-4110-9d9a-b619fccd88a6");
//  private static final UUID EVENT_ID = UUID.fromString("1091df64-40ea-427b-b570-587372241ce8");
//
//  @Test
//  void shouldCreateOutboxKey_whenValidAggregateIdAndEventId() {
//    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);
//
//    assertThat(key.partitionKey()).isEqualTo(OutboxKey.PARTITION_KEY_TEMPLATE.formatted(AGGREGATE_ID.asString()));
//    assertThat(key.sortKey()).isEqualTo(OutboxKey.SORT_KEY_TEMPLATE.formatted(EVENT_ID.toString()));
//  }
//
//  @Test
//  void shouldPartitionKeyStartWithBankAccountPrefix_whenValidAggregateId() {
//    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);
//
//    assertThat(key.partitionKey()).startsWith("BANK_ACCOUNT#");
//  }
//
//  @Test
//  void shouldSortKeyStartWithOutboxPrefix_whenValidEventId() {
//    var key = OutboxKey.of(AGGREGATE_ID, EVENT_ID);
//
//    assertThat(key.sortKey()).startsWith("OUTBOX#");
//  }
//
//  @ParameterizedTest
//  @NullSource
//  @ArgumentsSource(BlankValuesArgumentProvider.class)
//  void shouldThrowIllegalArgumentException_whenAggregateIdIsBlank(String blankAggregateId) {
//    assertThatThrownBy(() -> OutboxKey.of(() -> blankAggregateId, EVENT_ID))
//      .isInstanceOf(IllegalArgumentException.class)
//      .hasMessage(OutboxKey.AGGREGATE_ID_REQUIRED);
//  }
//
//  @Test
//  void shouldThrowIllegalArgumentException_whenEventIdIsNull() {
//    assertThatThrownBy(() -> OutboxKey.of(AGGREGATE_ID, null))
//      .isInstanceOf(IllegalArgumentException.class)
//      .hasMessage(OutboxKey.EVENT_ID_REQUIRED);
//  }
}