package com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions;

public final class EventPublicationFactoryNotFoundException extends IllegalStateException {

  public EventPublicationFactoryNotFoundException(Class<?> eventType) {
    super("No EventPublication factory registered for domain event type: " + eventType.getName());
  }
}
