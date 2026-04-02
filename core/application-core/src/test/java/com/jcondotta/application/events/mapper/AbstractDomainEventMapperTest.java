package com.jcondotta.application.events.mapper;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.application.testsupport.FakeAggregateId;
import com.jcondotta.application.testsupport.FakeDomainEvent;
import com.jcondotta.application.testsupport.FakeDomainEventMapper;
import com.jcondotta.application.testsupport.FakeIntegrationPayload;
import com.jcondotta.domain.identity.EventId;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractDomainEventMapperTest {

  private final FakeDomainEventMapper mapper = new FakeDomainEventMapper();

  private IntegrationEventMetadata metadata;
  private FakeDomainEvent event;

  private final CorrelationIdProvider correlationIdProvider = UUID::randomUUID;
  private final EventSourceProvider eventSourceProvider = () -> "test-event-source";

  @BeforeEach
  void setUp() {
    event = new FakeDomainEvent(EventId.newId(), FakeAggregateId.newId(), Instant.now());

    metadata = IntegrationEventMetadata.of(
      event.eventId().value(),
      correlationIdProvider.get(),
      eventSourceProvider.get(),
      event.occurredAt()
    );
  }

  @Test
  void shouldReturnCorrectDomainEventType_whenMapperIsCreated() {
    assertThat(mapper.domainEventType()).isEqualTo(FakeDomainEvent.class);
  }

  @Test
  void shouldMapDomainEventToIntegrationEvent_whenEventIsValid() {
    IntegrationEvent<?> integrationEvent = mapper.map(metadata, event);

    assertThat(integrationEvent.metadata()).isEqualTo(metadata);

    assertThat(integrationEvent.payload())
      .asInstanceOf(InstanceOfAssertFactories.type(FakeIntegrationPayload.class))
      .extracting(FakeIntegrationPayload::id)
      .isEqualTo(event.aggregateId().value());
  }
}