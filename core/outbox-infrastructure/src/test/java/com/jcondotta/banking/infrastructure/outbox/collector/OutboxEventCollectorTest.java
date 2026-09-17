package com.jcondotta.banking.infrastructure.outbox.collector;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationRegistry;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import com.jcondotta.banking.infrastructure.outbox.mapper.OutboxEntityMapper;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import com.jcondotta.domain.core.AggregateRoot;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
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
  private static final EventId FIRST_EVENT_ID = EventId.of(UUID.fromString("87d05b70-af57-4a31-ab01-d68053d1a1e7"));
  private static final EventId SECOND_EVENT_ID = EventId.of(UUID.fromString("c9ff04db-7d0c-4bc7-811b-4d1844fffb78"));
  private static final TestAggregateId AGGREGATE_ID =
    new TestAggregateId(UUID.fromString("694f92d6-b6f2-4ee6-b14a-4bc77949a86f"));
  private static final Instant FIRST_OCCURRED_AT = Instant.parse("2026-01-01T10:15:30Z");
  private static final Instant SECOND_OCCURRED_AT = Instant.parse("2026-01-01T10:15:31Z");
  private static final String EVENT_TYPE = "bank-account-opened";
  private static final String EVENT_SOURCE = "accounts";
  private static final String DESTINATION = "test-topic";
  private static final String FIRST_ROUTING_KEY = "bank-account-1";
  private static final String SECOND_ROUTING_KEY = "bank-account-2";
  private static final TestDomainEvent FIRST_EVENT = domainEvent(FIRST_EVENT_ID, FIRST_OCCURRED_AT, "first-event-data");
  private static final TestDomainEvent SECOND_EVENT = domainEvent(SECOND_EVENT_ID, SECOND_OCCURRED_AT, "second-event-data");

  private interface StubEntity extends OutboxRecord {}

  private record TestAggregateId(UUID value) implements AggregateId<UUID> {}

  private record TestDomainEvent(DomainEventMetadata<TestAggregateId> metadata, String data)
    implements DomainEvent<TestAggregateId, String> {

    @Override
    public String eventType() {
      return EVENT_TYPE;
    }
  }

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

    verifyNoInteractions(publicationRegistry, outboxEntityMapper, correlationIdProvider, eventSourceProvider);
  }

  @Test
  void shouldReturnSingleOutboxEntity_whenAggregateHasOneDomainEvent() {
    var routing = new EventRouting(DESTINATION, FIRST_ROUTING_KEY);
    var outboxEntity = mock(StubEntity.class);

    when(aggregate.pullEvents()).thenReturn(List.of(FIRST_EVENT));
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    when(publicationRegistry.routingFor(FIRST_EVENT)).thenReturn(routing);
    when(outboxEntityMapper.toOutboxEntity(any(EventPublication.class))).thenReturn(outboxEntity);

    assertThat(collector.collect(aggregate)).containsExactly(outboxEntity);
    var publicationCaptor = ArgumentCaptor.forClass(EventPublication.class);
    verify(outboxEntityMapper).toOutboxEntity(publicationCaptor.capture());
    assertThat(publicationCaptor.getValue()).isEqualTo(publicationFor(FIRST_EVENT, routing));
  }

  @Test
  void shouldReturnOutboxEntitiesForEachDomainEvent_whenAggregateHasMultipleDomainEvents() {
    var routing1 = new EventRouting(DESTINATION, FIRST_ROUTING_KEY);
    var routing2 = new EventRouting(DESTINATION, SECOND_ROUTING_KEY);
    var outboxEntity1 = mock(StubEntity.class);
    var outboxEntity2 = mock(StubEntity.class);

    when(aggregate.pullEvents()).thenReturn(List.of(FIRST_EVENT, SECOND_EVENT));
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    when(publicationRegistry.routingFor(FIRST_EVENT)).thenReturn(routing1);
    when(publicationRegistry.routingFor(SECOND_EVENT)).thenReturn(routing2);
    when(outboxEntityMapper.toOutboxEntity(any(EventPublication.class)))
      .thenReturn(outboxEntity1, outboxEntity2);

    assertThat(collector.collect(aggregate)).containsExactly(outboxEntity1, outboxEntity2);
    var publicationCaptor = ArgumentCaptor.forClass(EventPublication.class);
    verify(outboxEntityMapper, times(2)).toOutboxEntity(publicationCaptor.capture());
    assertThat(publicationCaptor.getAllValues())
      .containsExactly(
        publicationFor(FIRST_EVENT, routing1),
        publicationFor(SECOND_EVENT, routing2)
      );
  }

  @Test
  void shouldBuildContextOnce_whenAggregateHasMultipleDomainEvents() {
    when(aggregate.pullEvents()).thenReturn(List.of(FIRST_EVENT, SECOND_EVENT));
    when(correlationIdProvider.get()).thenReturn(CORRELATION_ID);
    when(eventSourceProvider.get()).thenReturn(EVENT_SOURCE);
    when(publicationRegistry.routingFor(FIRST_EVENT))
      .thenReturn(new EventRouting(DESTINATION, FIRST_ROUTING_KEY));
    when(publicationRegistry.routingFor(SECOND_EVENT))
      .thenReturn(new EventRouting(DESTINATION, SECOND_ROUTING_KEY));
    when(outboxEntityMapper.toOutboxEntity(any())).thenReturn(mock(StubEntity.class));

    collector.collect(aggregate);

    verify(correlationIdProvider, times(1)).get();
    verify(eventSourceProvider, times(1)).get();
  }

  private static TestDomainEvent domainEvent(EventId eventId, Instant occurredAt, String data) {
    return new TestDomainEvent(DomainEventMetadata.of(eventId, AGGREGATE_ID, occurredAt), data);
  }

  private static EventPublication publicationFor(TestDomainEvent event, EventRouting routing) {
    var envelope = new EventEnvelope(
      event.eventId().value().toString(),
      CORRELATION_ID,
      AGGREGATE_ID.asString(),
      EVENT_TYPE,
      EVENT_SOURCE,
      event.occurredAt(),
      1,
      event.data()
    );
    return new EventPublication(envelope, routing);
  }
}
