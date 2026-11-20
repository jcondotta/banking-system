package com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions;

public final class DuplicateEventPublicationFactoryException extends IllegalStateException {

  public DuplicateEventPublicationFactoryException(Class<?> eventType) {
    super("Multiple EventPublication factories registered for domain event type: " + eventType.getName());
  }
}
