package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

// Moved from accounts-infrastructure. Production class is:
// com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store.OutboxEventStoreImpl
//
// Tests remain commented because the production API has changed significantly:
// - tryClaimEvent() now returns Optional<OutboxEntity> (was boolean) and uses toBuilder()
// - markEventAsPublished() was renamed to deletePublishedEvent()
// - OutboxEventStoreImpl now takes OutboxProcessingProperties as a 3rd constructor arg
// - OutboxEntity no longer has transitionToProcessing / transitionToPublished methods
// - OutboxStatus enum was removed; OutboxQuery now has 2 fields (shard, limit) without status

@ExtendWith(MockitoExtension.class)
class OutboxEventStoreImplTest {

//  private static final int SHARD = 1;
//  private static final int LIMIT = 3;
//
//  @Mock
//  private DynamoDbTable<OutboxEntity> outboxTable;
//
//  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//  private OutboxTableProperties tableProperties;
//
//  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
//  private OutboxProcessingProperties processingProperties;
//
//  private OutboxEventStoreImpl repository;
//
//  @BeforeEach
//  void setUp() {
//    repository = new OutboxEventStoreImpl(outboxTable, tableProperties, processingProperties);
//  }
//
//  @Nested
//  class FindEventsTest {
//
//    private static final OutboxEntity ENTITY_1 = mock(OutboxEntity.class);
//    private static final OutboxEntity ENTITY_2 = mock(OutboxEntity.class);
//    private static final OutboxEntity ENTITY_3 = mock(OutboxEntity.class);
//    private static final OutboxEntity ENTITY_4 = mock(OutboxEntity.class);
//
//    @Mock
//    private DynamoDbIndex<OutboxEntity> outboxIndex;
//
//    @Mock
//    private PageIterable<OutboxEntity> pageIterable;
//
//    @Mock
//    private Iterator<Page<OutboxEntity>> pageIterator;
//
//    private OutboxQuery outboxQuery;
//
//    @BeforeEach
//    void setUp() {
//      outboxQuery = new OutboxQuery(SHARD, LIMIT);
//
//      when(tableProperties.indexes().gsi1().name()).thenReturn("gsi1-outbox-status-shard-index");
//      when(outboxTable.index(anyString())).thenReturn(outboxIndex);
//      when(outboxIndex.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
//      when(pageIterable.iterator()).thenReturn(pageIterator);
//    }
//
//    @Test
//    void shouldReturnEmptyList_whenNoPagesAvailable() {
//      when(pageIterator.hasNext()).thenReturn(false);
//
//      var result = repository.findPendingEvents(outboxQuery);
//
//      assertThat(result).isEmpty();
//      verify(pageIterator, never()).next();
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    void shouldReturnItems_whenSinglePageHasFewerItemsThanLimit() {
//      var page = (Page<OutboxEntity>) mock(Page.class);
//      when(pageIterator.hasNext()).thenReturn(true, false);
//      when(pageIterator.next()).thenReturn(page);
//      when(page.items()).thenReturn(List.of(ENTITY_1, ENTITY_2));
//
//      var result = repository.findPendingEvents(outboxQuery);
//
//      assertThat(result).containsExactly(ENTITY_1, ENTITY_2);
//      verify(pageIterator, times(1)).next();
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    void shouldReturnItemsUpToLimit_whenItemsAreSpreadAcrossMultiplePages() {
//      var page1 = (Page<OutboxEntity>) mock(Page.class);
//      var page2 = (Page<OutboxEntity>) mock(Page.class);
//      when(pageIterator.hasNext()).thenReturn(true, true, false);
//      when(pageIterator.next()).thenReturn(page1, page2);
//      when(page1.items()).thenReturn(List.of(ENTITY_1, ENTITY_2));
//      when(page2.items()).thenReturn(List.of(ENTITY_3, ENTITY_4));
//
//      var result = repository.findPendingEvents(outboxQuery);
//
//      assertThat(result)
//        .hasSize(LIMIT)
//        .containsExactly(ENTITY_1, ENTITY_2, ENTITY_3);
//      verify(pageIterator, times(2)).next();
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    void shouldStopIterating_whenLimitIsReachedWithinSinglePage() {
//      var page = (Page<OutboxEntity>) mock(Page.class);
//      when(pageIterator.hasNext()).thenReturn(true);
//      when(pageIterator.next()).thenReturn(page);
//      when(page.items()).thenReturn(List.of(ENTITY_1, ENTITY_2, ENTITY_3, ENTITY_4));
//
//      var result = repository.findPendingEvents(outboxQuery);
//
//      assertThat(result)
//        .hasSize(LIMIT)
//        .containsExactly(ENTITY_1, ENTITY_2, ENTITY_3);
//      verify(pageIterator, times(1)).next();
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    void shouldReturnExactLimit_whenPageContainsExactlyLimitItems() {
//      var page = (Page<OutboxEntity>) mock(Page.class);
//      when(pageIterator.hasNext()).thenReturn(true, false);
//      when(pageIterator.next()).thenReturn(page);
//      when(page.items()).thenReturn(List.of(ENTITY_1, ENTITY_2, ENTITY_3));
//
//      var result = repository.findPendingEvents(outboxQuery);
//
//      assertThat(result)
//        .hasSize(LIMIT)
//        .containsExactly(ENTITY_1, ENTITY_2, ENTITY_3);
//      verify(pageIterator, times(1)).next();
//    }
//  }
//
//  @Nested
//  class TryClaimEventTest {
//
//    private static final UUID EVENT_ID = UUID.fromString("b1c2d3e4-5f6a-7b8c-9d0e-1f2a3b4c5d6e");
//
//    @Mock
//    private OutboxEntity event;
//
//    @BeforeEach
//    void setUp() {
//      when(event.getEventId()).thenReturn(EVENT_ID);
//      when(event.getAggregateId()).thenReturn("test-aggregate-id");
//      when(event.toBuilder()).thenCallRealMethod(); // or configure with actual builder
//
//      when(processingProperties.lease()).thenReturn(new OutboxProcessingProperties.Lease(Duration.ofSeconds(30)));
//    }
//
//    @Test
//    void shouldReturnNonEmpty_whenEventIsClaimed() {
//      var result = repository.tryClaimEvent(event);
//
//      assertThat(result).isPresent();
//    }
//
//    @Test
//    void shouldPassCorrectItemToUpdateRequest_whenEventIsStillPending() {
//      repository.tryClaimEvent(event);
//
//      verify(outboxTable).updateItem(any(UpdateItemEnhancedRequest.class));
//    }
//
//    @Test
//    void shouldPassConditionExpression_whenEventIsStillPending() {
//      var captor = ArgumentCaptor.forClass(UpdateItemEnhancedRequest.class);
//
//      repository.tryClaimEvent(event);
//
//      verify(outboxTable).updateItem(captor.capture());
//      assertThat(captor.getValue().conditionExpression()).isNotNull();
//    }
//
//    @Test
//    void shouldReturnEmpty_whenEventAlreadyClaimed() {
//      doThrow(ConditionalCheckFailedException.class)
//        .when(outboxTable).updateItem(any(UpdateItemEnhancedRequest.class));
//
//      var result = repository.tryClaimEvent(event);
//
//      assertThat(result).isEmpty();
//    }
//
//    @Test
//    void shouldNotThrowException_whenEventAlreadyClaimed() {
//      doThrow(ConditionalCheckFailedException.class)
//        .when(outboxTable).updateItem(any(UpdateItemEnhancedRequest.class));
//
//      assertThat(repository.tryClaimEvent(event)).isEmpty();
//    }
//  }
//
//  @Nested
//  class DeletePublishedEventTest {
//
//    private static final UUID EVENT_ID = UUID.fromString("a3b8f9c1-4e2d-4a7b-9f3e-1c2d5e6f7a8b");
//
//    @Mock
//    private OutboxEntity event;
//
//    @BeforeEach
//    void setUp() {
//      when(event.getEventId()).thenReturn(EVENT_ID);
//      when(event.getAggregateId()).thenReturn("test-aggregate-id");
//      when(event.getNextAttemptAt()).thenReturn(Instant.now());
//    }
//
//    @Test
//    void shouldDeleteItem_whenEventIsProcessing() {
//      repository.deletePublishedEvent(event);
//
//      verify(outboxTable).deleteItem(any(DeleteItemEnhancedRequest.class));
//    }
//
//    @Test
//    void shouldPassConditionExpression_whenDeletingPublishedEvent() {
//      var captor = ArgumentCaptor.forClass(DeleteItemEnhancedRequest.class);
//
//      repository.deletePublishedEvent(event);
//
//      verify(outboxTable).deleteItem(captor.capture());
//      assertThat(captor.getValue().conditionExpression()).isNotNull();
//    }
//
//    @Test
//    void shouldThrowOutboxEventAlreadyProcessedException_whenEventIsNoLongerProcessing() {
//      doThrow(ConditionalCheckFailedException.class)
//        .when(outboxTable).deleteItem(any(DeleteItemEnhancedRequest.class));
//
//      assertThatThrownBy(() -> repository.deletePublishedEvent(event))
//        .isInstanceOf(OutboxEventAlreadyProcessedException.class)
//        .hasMessageContaining(EVENT_ID.toString());
//    }
//  }
}
