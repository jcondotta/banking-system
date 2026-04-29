package com.jcondotta.application.events.mapper;

import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.application.events.mapper.exceptions.DomainEventMapperNotFoundException;
import com.jcondotta.application.testsupport.FakeAggregateId;
import com.jcondotta.application.testsupport.FakeDomainEvent;
import com.jcondotta.application.testsupport.FakeDomainEventMapper;
import com.jcondotta.application.testsupport.FakeIntegrationPayload;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.EventId;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainEventMapperRegistryTest {

  private final FakeDomainEventMapper fakeMapper = new FakeDomainEventMapper();

  private FakeDomainEvent event;
  private IntegrationEventMetadata metadata;

  @BeforeEach
  void setUp() {
    event = new FakeDomainEvent(EventId.newId(), FakeAggregateId.newId(), Instant.now());
    metadata = IntegrationEventMetadata.of(
      event.eventId().value(),
      UUID.randomUUID(),
      "test-service",
      event.version(),
      event.occurredAt()
    );
  }

  @Test
  void shouldMapEventToIntegrationEvent_whenMapperExists() {
    var registry = new DomainEventMapperRegistry(
      Map.of(fakeMapper.domainEventType(), fakeMapper)
    );

    var integrationEvent = registry.map(metadata, event);

    assertThat(integrationEvent).isNotNull();
    assertThat(integrationEvent.metadata()).isEqualTo(metadata);
    assertThat(integrationEvent.payload())
      .asInstanceOf(InstanceOfAssertFactories.type(FakeIntegrationPayload.class))
      .extracting(FakeIntegrationPayload::id)
      .isEqualTo(event.aggregateId().value());
  }

  @Test
  void shouldThrowDomainEventMapperNotFoundException_whenNoMapperRegistered() {
    var registry = new DomainEventMapperRegistry(Map.of());

    assertThatThrownBy(() -> registry.map(metadata, event))
      .isInstanceOf(DomainEventMapperNotFoundException.class)
      .hasMessageContaining(event.getClass().getSimpleName());
  }

  @Test
  void shouldThrowException_whenEventIsNull() {
    var registry = new DomainEventMapperRegistry(
      Map.of(fakeMapper.domainEventType(), fakeMapper)
    );

    assertThatThrownBy(() -> registry.map(metadata, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(DomainEventMapperRegistry.EVENT_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldThrowException_whenRegistryIsNull() {
    assertThatThrownBy(() -> new DomainEventMapperRegistry(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(DomainEventMapperRegistry.REGISTRY_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldKeepRegistryImmutable_whenExternalMapChanges() {
    Map<Class<? extends DomainEvent<?>>, DomainEventIntegrationEventMapper<?, ?>> map = new HashMap<>();
    map.put(fakeMapper.domainEventType(), fakeMapper);

    var registry = new DomainEventMapperRegistry(map);
    map.clear();

    var integrationEvent = registry.map(metadata, event);

    assertThat(integrationEvent).isNotNull();
  }
}
