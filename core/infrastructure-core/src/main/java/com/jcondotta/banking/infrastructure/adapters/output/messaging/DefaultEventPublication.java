package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.domain.events.DomainEvent;

public record DefaultEventPublication<E extends DomainEvent<?, ?>>(
  E event,
  String destination,
  String key
) implements EventPublication<E> {

  public DefaultEventPublication {
    if (event == null) {
      throw new IllegalArgumentException("event must not be null");
    }
    if (destination == null || destination.isBlank()) {
      throw new IllegalArgumentException("destination must not be blank");
    }
    if (key == null || key.isBlank()) {
      throw new IllegalArgumentException("key must not be blank");
    }
  }
}
