package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.processor;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

// Moved from accounts-infrastructure.
//
// Tests remain commented because VirtualThreadOutboxProcessor no longer exists.
// The equivalent functionality is now in VirtualThreadOutboxDispatcher.
// OutboxEventClaimer, OutboxProperties, and OutboxStatus were also removed.
//
// To restore these tests: adapt to test VirtualThreadOutboxDispatcher.dispatch() with
// OutboxEventStore and OutboxEventShardProcessor mocks.

@ExtendWith(MockitoExtension.class)
class VirtualThreadOutboxProcessorTest {

//  private static final int SHARD_COUNT = 2;
//  private static final int BATCH_SIZE = 5;
//
//  @Mock
//  private OutboxEventStore eventStore;
//
//  @Mock
//  private OutboxEventShardProcessor eventShardProcessor;
//
//  @Mock
//  private OutboxShardsProperties shardsProperties;
//
//  private VirtualThreadOutboxDispatcher dispatcher;
//
//  @BeforeEach
//  void setUp() {
//    dispatcher = new VirtualThreadOutboxDispatcher(eventStore, eventShardProcessor, shardsProperties);
//
//    when(shardsProperties.shardIds()).thenReturn(Set.of(0, 1));
//    when(shardsProperties.batchSizePerShard()).thenReturn(BATCH_SIZE);
//  }
//
//  @Test
//  void shouldQueryPendingEventsForEachShard_whenDispatchIsTriggered() {
//    when(eventStore.findPendingEvents(any(OutboxQuery.class))).thenReturn(List.of());
//
//    dispatcher.dispatch();
//
//    verify(eventStore).findPendingEvents(new OutboxQuery(0, BATCH_SIZE));
//    verify(eventStore).findPendingEvents(new OutboxQuery(1, BATCH_SIZE));
//  }
//
//  @Test
//  void shouldNotProcessAnyEvent_whenNoPendingEventsExistForAnyShard() {
//    when(eventStore.findPendingEvents(any(OutboxQuery.class))).thenReturn(List.of());
//
//    dispatcher.dispatch();
//
//    verifyNoInteractions(eventShardProcessor);
//  }
//
//  @Test
//  void shouldProcessEvent_whenOnePendingEventExists() throws InterruptedException {
//    var event = buildOutboxEntity(UUID.randomUUID());
//
//    when(eventStore.findPendingEvents(new OutboxQuery(0, BATCH_SIZE))).thenReturn(List.of(event));
//    when(eventStore.findPendingEvents(new OutboxQuery(1, BATCH_SIZE))).thenReturn(List.of());
//
//    dispatcher.dispatch();
//
//    verify(eventShardProcessor, timeout(2000)).process(event);
//  }
//
//  @Test
//  void shouldProcessAllEvents_whenMultiplePendingEventsExistAcrossShards() throws InterruptedException {
//    var eventShard0a = buildOutboxEntity(UUID.randomUUID());
//    var eventShard0b = buildOutboxEntity(UUID.randomUUID());
//    var eventShard1 = buildOutboxEntity(UUID.randomUUID());
//
//    when(eventStore.findPendingEvents(new OutboxQuery(0, BATCH_SIZE))).thenReturn(List.of(eventShard0a, eventShard0b));
//    when(eventStore.findPendingEvents(new OutboxQuery(1, BATCH_SIZE))).thenReturn(List.of(eventShard1));
//
//    dispatcher.dispatch();
//
//    verify(eventShardProcessor, timeout(2000)).process(eventShard0a);
//    verify(eventShardProcessor, timeout(2000)).process(eventShard0b);
//    verify(eventShardProcessor, timeout(2000)).process(eventShard1);
//  }
//
//  @Test
//  void shouldUseBatchSizeFromProperties_whenQueryingPendingEvents() {
//    when(eventStore.findPendingEvents(any(OutboxQuery.class))).thenReturn(List.of());
//
//    dispatcher.dispatch();
//
//    verify(eventStore, times(SHARD_COUNT)).findPendingEvents(
//      argThat(query -> query.limit() == BATCH_SIZE)
//    );
//  }
//
//  @Test
//  void shouldProcessOnlySingleShard_whenOnlyOneShardIsConfigured() {
//    when(shardsProperties.shardIds()).thenReturn(Set.of(0));
//    when(eventStore.findPendingEvents(any(OutboxQuery.class))).thenReturn(List.of());
//
//    dispatcher.dispatch();
//
//    verify(eventStore, times(1)).findPendingEvents(any(OutboxQuery.class));
//  }
//
//  // ── helpers ──────────────────────────────────────────────────────────────
//
//  private static OutboxEntity buildOutboxEntity(UUID eventId) {
//    return OutboxEntity.builder()
//      .eventId(eventId)
//      .aggregateId(UUID.randomUUID().toString())
//      .eventType("BankAccountOpened")
//      .payload("{}")
//      .build();
//  }
}
