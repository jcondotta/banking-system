package com.jcondotta.application.core.events;

import com.jcondotta.application.core.events.mapper.DomainEventMapperRegistry;
import com.jcondotta.application.core.events.mapper.DomainEventMapperRegistryFactory;
import com.jcondotta.application.core.testsupport.FakeAggregateId;
import com.jcondotta.application.core.testsupport.FakeDomainEvent;
import com.jcondotta.application.core.testsupport.FakeDomainEventMapper;
import com.jcondotta.application.core.testsupport.FakeIntegrationPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationEventCollectorTest {

  private static final UUID CORRELATION_ID = UUID.randomUUID();
  private static final String EVENT_SOURCE = "test-event-source";

  private IntegrationEventCollector collector;

  @BeforeEach
  void setUp() {
    var mapper = new FakeDomainEventMapper();

    DomainEventMapperRegistry mapperRegistry = DomainEventMapperRegistryFactory.of(mapper);
    CorrelationIdProvider correlationIdProvider = () -> CORRELATION_ID;
    EventSourceProvider eventSourceProvider = () -> EVENT_SOURCE;

    collector = new IntegrationEventCollector(mapperRegistry, correlationIdProvider, eventSourceProvider);
  }

  @Test
  void shouldCollectIntegrationEvents_whenDomainEventsProvided() {
    var event = FakeDomainEvent.newEvent(FakeAggregateId.newId());

    var integrationEvents = collector.collect(List.of(event));

    assertThat(integrationEvents)
      .hasSize(1)
      .singleElement()
      .satisfies(integrationEvent -> {
        assertThat(integrationEvent.metadata().correlationId()).isEqualTo(CORRELATION_ID);
        assertThat(integrationEvent.metadata().eventSource()).isEqualTo(EVENT_SOURCE);
        assertThat(integrationEvent.metadata().eventId()).isEqualTo(event.eventId().value());
        assertThat(integrationEvent.metadata().version()).isEqualTo(event.version());
        assertThat(integrationEvent.metadata().occurredAt()).isEqualTo(event.occurredAt());
      });
  }

  @Test
  void shouldMapPayloadCorrectly_whenEventIsProcessed() {
    var event = FakeDomainEvent.newEvent(FakeAggregateId.newId());

    var integrationEvents = collector.collect(List.of(event));

    assertThat(integrationEvents)
      .hasSize(1)
      .singleElement()
      .satisfies(integrationEvent -> {
        assertThat(integrationEvent.payload()).isInstanceOf(FakeIntegrationPayload.class);

        var payload = (FakeIntegrationPayload) integrationEvent.payload();

        assertThat(payload.id()).isEqualTo(event.aggregateId().value());
      });
  }

  @Test
  void shouldReturnEmptyList_whenNoDomainEventsProvided() {
    var integrationEvents = collector.collect(List.of());

    assertThat(integrationEvents).isEmpty();
  }

  @Test
  void shouldCollectMultipleEvents_whenMultipleDomainEventsProvided() {
    var event1 = FakeDomainEvent.newEvent(FakeAggregateId.newId());
    var event2 = FakeDomainEvent.newEvent(FakeAggregateId.newId());

    var integrationEvents = collector.collect(List.of(event1, event2));

    assertThat(integrationEvents).hasSize(2);
  }
}