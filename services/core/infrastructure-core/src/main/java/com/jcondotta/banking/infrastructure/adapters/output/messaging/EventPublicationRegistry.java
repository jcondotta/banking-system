package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions.EventPublicationFactoryNotFoundException;
import com.jcondotta.domain.events.DomainEvent;

import java.util.Map;

public final class EventPublicationRegistry {

  private final Map<Class<? extends DomainEvent<?, ?>>, EventRoutingResolver<?>> factories;

  public EventPublicationRegistry(Map<Class<? extends DomainEvent<?, ?>>, EventRoutingResolver<?>> factories) {
    if (factories == null) {
      throw new IllegalArgumentException("registry must not be null");
    }
    this.factories = Map.copyOf(factories);
  }

  public EventRouting routingFor(DomainEvent<?, ?> event) {
    if (event == null) {
      throw new IllegalArgumentException("event must not be null");
    }

    var factory = factories.get(event.getClass());
    if (factory == null) {
      throw new EventPublicationFactoryNotFoundException(event.getClass());
    }

    return create(factory, event);
  }

  @SuppressWarnings("unchecked")
  private static <E extends DomainEvent<?, ?>> EventRouting create(EventRoutingResolver<?> factory, DomainEvent<?, ?> event) {
    return ((EventRoutingResolver<E>) factory).resolve((E) event);
  }
}
