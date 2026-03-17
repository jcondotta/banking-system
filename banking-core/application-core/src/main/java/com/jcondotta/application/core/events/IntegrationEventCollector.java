package com.jcondotta.application.core.events;

import com.jcondotta.application.core.events.mapper.DomainEventMapperRegistry;
import com.jcondotta.domain.events.DomainEvent;

import java.util.List;
import java.util.stream.Collectors;

public class IntegrationEventCollector {

  private final DomainEventMapperRegistry mapperRegistry;
  private final CorrelationIdProvider correlationIdProvider;
  private final EventSourceProvider eventSourceProvider;

  public IntegrationEventCollector(DomainEventMapperRegistry mapperRegistry, CorrelationIdProvider correlationIdProvider, EventSourceProvider eventSourceProvider) {
    this.mapperRegistry = mapperRegistry;
    this.correlationIdProvider = correlationIdProvider;
    this.eventSourceProvider = eventSourceProvider;
  }

  public List<IntegrationEvent<?>> collect(List<DomainEvent<?>> domainEvents) {
    return domainEvents.stream()
      .map(this::toIntegrationEvent)
      .collect(Collectors.toList());
  }

  private IntegrationEvent<?> toIntegrationEvent(DomainEvent<?> event) {
    return mapperRegistry.map(metadataFor(event), event);
  }

  private IntegrationEventMetadata metadataFor(DomainEvent<?> event) {
    return IntegrationEventMetadata.of(
      event.eventId().value(),
      correlationIdProvider.get(),
      eventSourceProvider.get(),
      event.version(),
      event.occurredAt()
    );
  }
}