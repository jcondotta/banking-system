package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.collector;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.collector.OutboxEventCollector;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.mapper.OutboxEntityMapper;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationRegistry;
import com.jcondotta.domain.core.AggregateRoot;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventCollectorTest {

  private static final UUID CORRELATION_ID = UUID.fromString("f7be51e6-0a9d-4789-91e2-bab5e7800247");
  private static final String EVENT_SOURCE = "accounts";

  @Mock
  private EventPublicationRegistry publicationRegistry;

  @Mock
  private OutboxEntityMapper outboxEntityMapper;

  @Mock
  private CorrelationIdProvider correlationIdProvider;

  @Mock
  private EventSourceProvider eventSourceProvider;

  @Mock
  private AggregateRoot<?> aggregate;

  private OutboxEventCollector collector;

  @BeforeEach
  void setUp() {
    collector = new OutboxEventCollector(publicationRegistry, outboxEntityMapper, correlationIdProvider, eventSourceProvider);
  }

  @Test
  void shouldReturnEmptyList_whenAggregateHasNoDomainEvents() {
    when(aggregate.pullEvents()).thenReturn(List.of());

    assertThat(collector.collect(aggregate)).isEmpty();

    verify(publicationRegistry, never()).publicationFor(any());
    verify(outboxEntityMapper, never()).toOutboxEntity(any(), any());
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldReturnSingleOutboxEntity_whenAggregateHasOneDomainEvent() {
    var event = mockDomainEvent();
    var publication = mockPublicationFor(event);
    var outboxEntity = mock(OutboxEntity.class);

    when(aggregate.pullEvents()).thenReturn(List.of(event));
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    doReturn(publication).when(publicationRegistry).publicationFor(event);
    when(outboxEntityMapper.toOutboxEntity(eq(event), any(EventEnvelope.class))).thenReturn(outboxEntity);

    assertThat(collector.collect(aggregate)).containsExactly(outboxEntity);
    verify(outboxEntityMapper).toOutboxEntity(eq(event), any(EventEnvelope.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldReturnOutboxEntitiesForEachDomainEvent_whenAggregateHasMultipleDomainEvents() {
    var event1 = mockDomainEvent();
    var event2 = mockDomainEvent();
    var publication1 = mockPublicationFor(event1);
    var publication2 = mockPublicationFor(event2);
    var outboxEntity1 = mock(OutboxEntity.class);
    var outboxEntity2 = mock(OutboxEntity.class);

    when(aggregate.pullEvents()).thenReturn(List.of(event1, event2));
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    doReturn(publication1).when(publicationRegistry).publicationFor(event1);
    doReturn(publication2).when(publicationRegistry).publicationFor(event2);
    when(outboxEntityMapper.toOutboxEntity(eq(event1), any(EventEnvelope.class))).thenReturn(outboxEntity1);
    when(outboxEntityMapper.toOutboxEntity(eq(event2), any(EventEnvelope.class))).thenReturn(outboxEntity2);

    assertThat(collector.collect(aggregate)).containsExactly(outboxEntity1, outboxEntity2);
    verify(outboxEntityMapper).toOutboxEntity(eq(event1), any(EventEnvelope.class));
    verify(outboxEntityMapper).toOutboxEntity(eq(event2), any(EventEnvelope.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldBuildContextOnce_whenAggregateHasMultipleDomainEvents() {
    var event1 = mockDomainEvent();
    var event2 = mockDomainEvent();

    when(aggregate.pullEvents()).thenReturn(List.of(event1, event2));
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    doReturn(mockPublicationFor(event1)).when(publicationRegistry).publicationFor(event1);
    doReturn(mockPublicationFor(event2)).when(publicationRegistry).publicationFor(event2);
    when(outboxEntityMapper.toOutboxEntity(any(), any())).thenReturn(mock(OutboxEntity.class));

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

  @SuppressWarnings("unchecked")
  private EventPublication<?> mockPublicationFor(DomainEvent<?, ?> event) {
    var publication = mock(EventPublication.class);
    lenient().when(publication.event()).thenReturn(event);
    return publication;
  }
}
