package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions.DuplicateEventPublicationFactoryException;
import com.jcondotta.domain.events.DomainEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventPublicationRegistryFactory {

  public EventPublicationRegistry create(List<EventPublicationFactory<?>> factories) {
    if (factories == null) {
      throw new IllegalArgumentException("eventPublicationFactories must not be null");
    }

    factories.forEach(factory -> {
      if (factory == null) {
        throw new IllegalArgumentException("eventPublicationFactory must not be null");
      }
      if (factory.domainEventType() == null) {
        throw new IllegalArgumentException("domainEventType must not be null");
      }
    });

    Map<Class<? extends DomainEvent<?, ?>>, EventPublicationFactory<?>> registry = new HashMap<>();
    factories.forEach(factory -> {
      if (registry.putIfAbsent(factory.domainEventType(), factory) != null) {
        throw new DuplicateEventPublicationFactoryException(factory.domainEventType());
      }
    });

    return new EventPublicationRegistry(registry);
  }

  public static EventPublicationRegistry of(EventPublicationFactory<?>... factories) {
    if (factories == null) {
      throw new IllegalArgumentException("eventPublicationFactories must not be null");
    }
    return new EventPublicationRegistryFactory().create(List.of(factories));
  }
}
