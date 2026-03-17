package com.jcondotta.application.core.events.mapper;

import com.jcondotta.application.core.events.IntegrationEvent;
import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.application.core.events.mapper.exceptions.DomainEventMapperNotFoundException;
import com.jcondotta.domain.events.DomainEvent;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class DomainEventMapperRegistry {

  static final String REGISTRY_MUST_NOT_BE_NULL = "registry must not be null";
  static final String EVENT_MUST_NOT_BE_NULL = "event must not be null";

  private final Map<Class<? extends DomainEvent<?>>, DomainEventIntegrationEventMapper<?, ?>> registry;

  public DomainEventMapperRegistry(
    Map<Class<? extends DomainEvent<?>>, DomainEventIntegrationEventMapper<?, ?>> registry) {

    requireNonNull(registry, REGISTRY_MUST_NOT_BE_NULL);
    this.registry = Map.copyOf(registry);
  }

  public IntegrationEvent<?> map(IntegrationEventMetadata metadata, DomainEvent<?> event) {
    requireNonNull(event, EVENT_MUST_NOT_BE_NULL);

    var rawMapper = registry.get(event.getClass());
    if (rawMapper == null) {
      throw new DomainEventMapperNotFoundException(event.getClass());
    }

    @SuppressWarnings("unchecked")
    var mapper = (DomainEventIntegrationEventMapper<DomainEvent<?>, ?>) rawMapper;
    return mapper.map(metadata, event);
  }
}