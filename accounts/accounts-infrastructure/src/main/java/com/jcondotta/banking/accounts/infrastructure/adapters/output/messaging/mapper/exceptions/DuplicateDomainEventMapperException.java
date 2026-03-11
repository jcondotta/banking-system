package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.exceptions;

public class DuplicateDomainEventMapperException extends RuntimeException {

  public DuplicateDomainEventMapperException(Class<?> eventType) {
    super("Multiple mappers registered for event type: " + eventType.getName());
  }
}