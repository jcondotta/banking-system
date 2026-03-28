package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.query;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.OutboxEventAlreadyProcessedException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.OutboxStatus;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxTableProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxRepositoryImplTest {

  private static final int SHARD = 1;
  private static final int LIMIT = 3;

  @Mock
  private DynamoDbTable<OutboxEntity> outboxTable;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private OutboxTableProperties tableProperties;

  private OutboxRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new OutboxRepositoryImpl(outboxTable, tableProperties);
  }

  @Nested
  class FindEventsTest {

    private static final OutboxEntity ENTITY_1 = mock(OutboxEntity.class);
    private static final OutboxEntity ENTITY_2 = mock(OutboxEntity.class);
    private static final OutboxEntity ENTITY_3 = mock(OutboxEntity.class);
    private static final OutboxEntity ENTITY_4 = mock(OutboxEntity.class);

    @Mock
    private DynamoDbIndex<OutboxEntity> outboxIndex;

    @Mock
    private PageIterable<OutboxEntity> pageIterable;

    @Mock
    private Iterator<Page<OutboxEntity>> pageIterator;

    private OutboxQuery outboxQuery;

    @BeforeEach
    void setUp() {
      outboxQuery = new OutboxQuery(SHARD, LIMIT, OutboxStatus.PENDING);

      when(tableProperties.indexes().gsi1().name()).thenReturn("gsi1-outbox-status-shard-index");
      when(outboxTable.index(anyString())).thenReturn(outboxIndex);
      when(outboxIndex.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);
      when(pageIterable.iterator()).thenReturn(pageIterator);
    }

    @Test
    void shouldReturnEmptyList_whenNoPagesAvailable() {
      when(pageIterator.hasNext()).thenReturn(false);

      var result = repository.findEvents(outboxQuery);

      assertThat(result).isEmpty();
      verify(pageIterator, never()).next();
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnItems_whenSinglePageLessThanLimit() {
      var page = (Page<OutboxEntity>) mock(Page.class);
      when(pageIterator.hasNext()).thenReturn(true, false);
      when(pageIterator.next()).thenReturn(page);
      when(page.items()).thenReturn(List.of(ENTITY_1, ENTITY_2));

      var result = repository.findEvents(outboxQuery);

      assertThat(result).containsExactly(ENTITY_1, ENTITY_2);
      verify(pageIterator, times(1)).next();
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnItemsUpToLimit_whenMultiplePagesAvailable() {
      var page1 = (Page<OutboxEntity>) mock(Page.class);
      var page2 = (Page<OutboxEntity>) mock(Page.class);
      when(pageIterator.hasNext()).thenReturn(true, true, false);
      when(pageIterator.next()).thenReturn(page1, page2);
      when(page1.items()).thenReturn(List.of(ENTITY_1, ENTITY_2));
      when(page2.items()).thenReturn(List.of(ENTITY_3, ENTITY_4));

      var result = repository.findEvents(outboxQuery);

      assertThat(result)
        .hasSize(LIMIT)
        .containsExactly(ENTITY_1, ENTITY_2, ENTITY_3);
      verify(pageIterator, times(2)).next();
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldStopIterating_whenLimitReachedBeforeConsumingAllPages() {
      var page = (Page<OutboxEntity>) mock(Page.class);
      when(pageIterator.hasNext()).thenReturn(true);
      when(pageIterator.next()).thenReturn(page);
      when(page.items()).thenReturn(List.of(ENTITY_1, ENTITY_2, ENTITY_3, ENTITY_4));

      var result = repository.findEvents(outboxQuery);

      assertThat(result)
        .hasSize(LIMIT)
        .containsExactly(ENTITY_1, ENTITY_2, ENTITY_3);
      verify(pageIterator, times(1)).next();
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldHandleExactLimitBoundary() {
      var page = (Page<OutboxEntity>) mock(Page.class);
      when(pageIterator.hasNext()).thenReturn(true, false);
      when(pageIterator.next()).thenReturn(page);
      when(page.items()).thenReturn(List.of(ENTITY_1, ENTITY_2, ENTITY_3));

      var result = repository.findEvents(outboxQuery);

      assertThat(result)
        .hasSize(LIMIT)
        .containsExactly(ENTITY_1, ENTITY_2, ENTITY_3);
      verify(pageIterator, times(1)).next();
    }
  }

  @Nested
  class MarkAsPublishedTest {

    private static final UUID EVENT_ID = UUID.fromString("a3b8f9c1-4e2d-4a7b-9f3e-1c2d5e6f7a8b");

    @Mock
    private OutboxEntity event;

    @Test
    void shouldMarkAsPublished_whenEventIsPending() {
      repository.markAsPublished(event);

      verify(event).markAsPublished(any(Instant.class));
      verify(outboxTable).updateItem(any(UpdateItemEnhancedRequest.class));
    }

    @Test
    void shouldPassCorrectItemToUpdateRequest_whenEventIsPending() {
      var captor = ArgumentCaptor.forClass(UpdateItemEnhancedRequest.class);

      repository.markAsPublished(event);

      verify(outboxTable).updateItem(captor.capture());
      assertThat(captor.getValue().item()).isEqualTo(event);
    }

    @Test
    void shouldPassConditionExpression_whenEventIsPending() {
      var captor = ArgumentCaptor.forClass(UpdateItemEnhancedRequest.class);

      repository.markAsPublished(event);

      verify(outboxTable).updateItem(captor.capture());
      assertThat(captor.getValue().conditionExpression()).isNotNull();
    }

    @Test
    void shouldThrowOutboxEventAlreadyProcessedException_whenEventIsNotPending() {
      when(event.getEventId()).thenReturn(EVENT_ID);
      doThrow(ConditionalCheckFailedException.class)
        .when(outboxTable).updateItem(any(UpdateItemEnhancedRequest.class));

      assertThatThrownBy(() -> repository.markAsPublished(event))
        .isInstanceOf(OutboxEventAlreadyProcessedException.class)
        .hasMessageContaining(EVENT_ID.toString());
    }

    @Test
    void shouldMarkAsPublishedBeforeUpdatingDynamoDB_whenEventIsPending() {
      var order = inOrder(event, outboxTable);

      repository.markAsPublished(event);

      order.verify(event).markAsPublished(any(Instant.class));
      order.verify(outboxTable).updateItem(any(UpdateItemEnhancedRequest.class));
    }
  }
}