package com.jcondotta.banking.infrastructure.outbox.collector;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.banking.infrastructure.outbox.collector.OutboxEventCollector;
import com.jcondotta.banking.infrastructure.outbox.mapper.OutboxEntityMapper;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationRegistry;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import com.jcondotta.domain.core.AggregateRoot;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventCollectorTest {

  private static final UUID CORRELATION_ID = UUID.fromString("f7be51e6-0a9d-4789-91e2-bab5e7800247");
  private static final String EVENT_SOURCE = "accounts";
  private static final String DESTINATION = "test-topic";

  private interface StubEntity extends OutboxRecord {}

  @Mock
  private EventPublicationRegistry publicationRegistry;

  @Mock
  private OutboxEntityMapper<StubEntity> outboxEntityMapper;

  @Mock
  private CorrelationIdProvider correlationIdProvider;

  @Mock
  private EventSourceProvider eventSourceProvider;

  @Mock
  private AggregateRoot<?> aggregate;

  private OutboxEventCollector<StubEntity> collector;

  @BeforeEach
  void setUp() {
    collector = new OutboxEventCollector<>(publicationRegistry, outboxEntityMapper, correlationIdProvider, eventSourceProvider);
  }

  @Test
  void shouldReturnEmptyList_whenAggregateHasNoDomainEvents() {
    when(aggregate.pullEvents()).thenReturn(List.of());

    assertThat(collector.collect(aggregate)).isEmpty();

    verify(publicationRegistry, never()).routingFor(any());
    verify(outboxEntityMapper, never()).toOutboxEntity(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldReturnSingleOutboxEntity_whenAggregateHasOneDomainEvent() {
    var event = mockDomainEvent();
    var routing = routingFor(event);
    var outboxEntity = mock(StubEntity.class);

    when(aggregate.pullEvents()).thenReturn(List.of(event));
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    doReturn(routing).when(publicationRegistry).routingFor(event);
    when(outboxEntityMapper.toOutboxEntity(any(EventPublication.class))).thenReturn(outboxEntity);

    assertThat(collector.collect(aggregate)).containsExactly(outboxEntity);
    var publicationCaptor = ArgumentCaptor.forClass(EventPublication.class);
    verify(outboxEntityMapper).toOutboxEntity(publicationCaptor.capture());
    assertThat(publicationCaptor.getValue().routing()).isEqualTo(routing);
    assertThat(publicationCaptor.getValue().envelope().correlationId()).isEqualTo(CORRELATION_ID);
    assertThat(publicationCaptor.getValue().envelope().eventSource()).isEqualTo(EVENT_SOURCE);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldReturnOutboxEntitiesForEachDomainEvent_whenAggregateHasMultipleDomainEvents() {
    var key1 = UUID.randomUUID().toString();
    var key2 = UUID.randomUUID().toString();
    var event1 = mockDomainEvent();
    var event2 = mockDomainEvent();
    var routing1 = new EventRouting(DESTINATION, key1);
    var routing2 = new EventRouting(DESTINATION, key2);
    var outboxEntity1 = mock(StubEntity.class);
    var outboxEntity2 = mock(StubEntity.class);

    when(aggregate.pullEvents()).thenReturn(List.of(event1, event2));
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    doReturn(routing1).when(publicationRegistry).routingFor(event1);
    doReturn(routing2).when(publicationRegistry).routingFor(event2);
    when(outboxEntityMapper.toOutboxEntity(any(EventPublication.class)))
      .thenReturn(outboxEntity1, outboxEntity2);

    assertThat(collector.collect(aggregate)).containsExactly(outboxEntity1, outboxEntity2);
    var publicationCaptor = ArgumentCaptor.forClass(EventPublication.class);
    verify(outboxEntityMapper, times(2)).toOutboxEntity(publicationCaptor.capture());
    assertThat(publicationCaptor.getAllValues())
      .extracting(EventPublication::routing)
      .containsExactly(routing1, routing2);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldBuildContextOnce_whenAggregateHasMultipleDomainEvents() {
    var event1 = mockDomainEvent();
    var event2 = mockDomainEvent();

    when(aggregate.pullEvents()).thenReturn(List.of(event1, event2));
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    doReturn(routingFor(event1)).when(publicationRegistry).routingFor(event1);
    doReturn(routingFor(event2)).when(publicationRegistry).routingFor(event2);
    when(outboxEntityMapper.toOutboxEntity(any())).thenReturn(mock(StubEntity.class));

    collector.collect(aggregate);

    verify(correlationIdProvider, times(1)).get();
    verify(eventSourceProvider, times(1)).get();
  }

  @SuppressWarnings("unchecked")
  private DomainEvent<?, ?> mockDomainEvent() {
    var event = mock(DomainEvent.class);
    var aggregateId = mock(AggregateId.class);

    lenient().when(aggregateId.asString()).thenReturn(UUID.randomUUID().toString());
    lenient().when(event.eventId()).thenReturn(EventId.newId());
    lenient().when(event.aggregateId()).thenReturn(aggregateId);
    lenient().when(event.eventType()).thenReturn("bank-account-opened");
    lenient().when(event.occurredAt()).thenReturn(Instant.now());
    lenient().when(event.eventVersion()).thenReturn(1);
    lenient().when(event.data()).thenReturn(new Object());

    return event;
  }

  private EventRouting routingFor(DomainEvent<?, ?> event) {
    return new EventRouting(DESTINATION, UUID.randomUUID().toString());
  }
}
