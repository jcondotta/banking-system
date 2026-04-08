package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.processor;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class VirtualThreadOutboxProcessorTest {

//  private static final int SHARD_COUNT = 2;
//  private static final int BATCH_SIZE = 5;
//
//  @Mock
//  private OutboxEventClaimer eventClaimer;
//
//  @Mock
//  private OutboxEventStore repository;
//
//  @Mock
//  private OutboxProperties properties;
//
//  @Mock
//  private OutboxShardsProperties outboxShardsProperties;
//
//  private VirtualThreadOutboxProcessor processor;
//
//  @BeforeEach
//  void setUp() {
//    processor = new VirtualThreadOutboxProcessor(eventClaimer, repository, properties);
//
//    when(properties.shards()).thenReturn(outboxShardsProperties);
//    when(outboxShardsProperties.shardIds()).thenReturn(Set.of(0, 1));
//    when(outboxShardsProperties.batchSizePerShard()).thenReturn(BATCH_SIZE);
//  }
//
//  @Test
//  void shouldQueryPendingEventsForEachShard_whenProcessingIsTriggered() {
//    when(repository.findPendingEvents(any(OutboxQuery.class))).thenReturn(List.of());
//
//    processor.process();
//
//    verify(repository).findPendingEvents(new OutboxQuery(0, BATCH_SIZE, OutboxStatus.PENDING));
//    verify(repository).findPendingEvents(new OutboxQuery(1, BATCH_SIZE, OutboxStatus.PENDING));
//  }
//
//  @Test
//  void shouldNotClaimAnyEvent_whenNoPendingEventsExistForAnyShard() {
//    when(repository.findPendingEvents(any(OutboxQuery.class))).thenReturn(List.of());
//
//    processor.process();
//
//    verifyNoInteractions(eventClaimer);
//  }
//
//  @Test
//  void shouldClaimEvent_whenOnePendingEventExists() throws InterruptedException {
//    var event = buildOutboxEntity(UUID.randomUUID());
//
//    when(repository.findPendingEvents(new OutboxQuery(0, BATCH_SIZE, OutboxStatus.PENDING)))
//      .thenReturn(List.of(event));
//    when(repository.findPendingEvents(new OutboxQuery(1, BATCH_SIZE, OutboxStatus.PENDING)))
//      .thenReturn(List.of());
//
//    processor.process();
//
//    // Virtual threads are async — wait for the submitted task to complete
//    verify(eventClaimer, timeout(2000)).claim(eq(0), eq(event));
//    verify(eventClaimer, never()).claim(eq(1), any());
//  }
//
//  @Test
//  void shouldClaimAllEvents_whenMultiplePendingEventsExistAcrossShards() throws InterruptedException {
//    var eventShard0a = buildOutboxEntity(UUID.randomUUID());
//    var eventShard0b = buildOutboxEntity(UUID.randomUUID());
//    var eventShard1 = buildOutboxEntity(UUID.randomUUID());
//
//    when(repository.findPendingEvents(new OutboxQuery(0, BATCH_SIZE, OutboxStatus.PENDING)))
//      .thenReturn(List.of(eventShard0a, eventShard0b));
//    when(repository.findPendingEvents(new OutboxQuery(1, BATCH_SIZE, OutboxStatus.PENDING)))
//      .thenReturn(List.of(eventShard1));
//
//    processor.process();
//
//    verify(eventClaimer, timeout(2000)).claim(eq(0), eq(eventShard0a));
//    verify(eventClaimer, timeout(2000)).claim(eq(0), eq(eventShard0b));
//    verify(eventClaimer, timeout(2000)).claim(eq(1), eq(eventShard1));
//  }
//
//  @Test
//  void shouldUseBatchSizeFromProperties_whenQueryingPendingEvents() {
//    when(repository.findPendingEvents(any(OutboxQuery.class))).thenReturn(List.of());
//
//    processor.process();
//
//    verify(repository, times(SHARD_COUNT)).findPendingEvents(
//      argThat(query -> query.limit() == BATCH_SIZE)
//    );
//  }
//
//  @Test
//  void shouldQueryWithPendingStatus_whenFetchingEventsForEachShard() {
//    when(repository.findPendingEvents(any(OutboxQuery.class))).thenReturn(List.of());
//
//    processor.process();
//
//    verify(repository, times(SHARD_COUNT)).findPendingEvents(
//      argThat(query -> query.status() == OutboxStatus.PENDING)
//    );
//  }
//
//  @Test
//  void shouldProcessOnlySingleShard_whenOnlyOneShardIsConfigured() {
//    when(outboxShardsProperties.shardIds()).thenReturn(Set.of(0));
//    when(repository.findPendingEvents(any(OutboxQuery.class))).thenReturn(List.of());
//
//    processor.process();
//
//    verify(repository, times(1)).findPendingEvents(any(OutboxQuery.class));
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
//      .outboxStatus(OutboxStatus.PENDING)
//      .build();
//  }
}
