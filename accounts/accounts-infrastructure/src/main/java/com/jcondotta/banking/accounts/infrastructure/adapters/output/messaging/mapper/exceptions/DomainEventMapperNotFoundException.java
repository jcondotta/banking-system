package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.exceptions;

public class DomainEventMapperNotFoundException extends RuntimeException {

  public DomainEventMapperNotFoundException(Class<?> eventType) {
    super("No mapper registered for event type: " + eventType.getName());
  }
}