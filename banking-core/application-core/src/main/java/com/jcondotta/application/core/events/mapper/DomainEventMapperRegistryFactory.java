package com.jcondotta.application.core.events.mapper;

import com.jcondotta.application.core.events.mapper.exceptions.DuplicateDomainEventMapperException;
import com.jcondotta.domain.events.DomainEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class DomainEventMapperRegistryFactory {

  static final String DOMAIN_EVENT_MAPPERS_MUST_NOT_BE_NULL = "domainEventMappers must not be null";
  static final String DOMAIN_EVENT_MAPPER_MUST_NOT_BE_NULL = "domainEventMapper must not be null";
  static final String DOMAIN_EVENT_TYPE_MUST_NOT_BE_NULL = "domainEventType must not be null";

  public DomainEventMapperRegistry create(List<DomainEventIntegrationEventMapper<?, ?>> mappers) {
    requireNonNull(mappers, DOMAIN_EVENT_MAPPERS_MUST_NOT_BE_NULL);

    mappers.forEach(mapper -> {
      requireNonNull(mapper, DOMAIN_EVENT_MAPPER_MUST_NOT_BE_NULL);
      requireNonNull(mapper.domainEventType(), DOMAIN_EVENT_TYPE_MUST_NOT_BE_NULL);
    });

    Map<Class<? extends DomainEvent<?>>, DomainEventIntegrationEventMapper<?, ?>> registry = new HashMap<>();

    mappers.forEach(mapper -> {
      if (registry.putIfAbsent(mapper.domainEventType(), mapper) != null) {
        throw new DuplicateDomainEventMapperException(mapper.domainEventType());
      }
    });

    return new DomainEventMapperRegistry(registry);
  }

  public static DomainEventMapperRegistry of(DomainEventIntegrationEventMapper<?, ?>... mappers) {
    requireNonNull(mappers, DOMAIN_EVENT_MAPPERS_MUST_NOT_BE_NULL);
    return new DomainEventMapperRegistryFactory().create(List.of(mappers));
  }
}