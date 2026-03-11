package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.exceptions.DomainEventMapperNotFoundException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.exceptions.DuplicateDomainEventMapperException;
import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.domain.events.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public final class DefaultDomainEventToIntegrationEventResolver implements DomainEventToIntegrationEventResolver {

  private final Map<Class<? extends DomainEvent>, DomainEventMapper<?>> registry;

  public DefaultDomainEventToIntegrationEventResolver(List<DomainEventMapper<?>> mappers) {
    Objects.requireNonNull(mappers, "domainEventMappers must not be null");
    this.registry = buildRegistry(mappers);
  }

  @Override
  public IntegrationEvent<?> toIntegrationEvent(DomainEvent event, EventMetadataContext context) {
    Objects.requireNonNull(event, "event must not be null");
    Objects.requireNonNull(context, "context must not be null");

    return resolve(event).toIntegrationEvent(event, context);
  }

  @SuppressWarnings("unchecked")
  private <E extends DomainEvent<?>> DomainEventMapper<E> resolve(E event) {
    DomainEventMapper<?> mapper = registry.get(event.getClass());

    if (mapper == null) {
      throw new DomainEventMapperNotFoundException(event.getClass());
    }

    return (DomainEventMapper<E>) mapper;
  }

  private Map<Class<? extends DomainEvent>, DomainEventMapper<?>> buildRegistry(List<DomainEventMapper<?>> mappers) {
    Map<Class<? extends DomainEvent>, DomainEventMapper<?>> map = new HashMap<>();

    for (DomainEventMapper<?> mapper : mappers) {
      Objects.requireNonNull(mapper, "domainEventMapper must not be null");
      Objects.requireNonNull(mapper.mappedEventType(), "mappedEventType must not be null");

      if (map.putIfAbsent(mapper.mappedEventType(), mapper) != null) {
        throw new DuplicateDomainEventMapperException(mapper.mappedEventType());
      }
    }

    return Map.copyOf(map);
  }
}