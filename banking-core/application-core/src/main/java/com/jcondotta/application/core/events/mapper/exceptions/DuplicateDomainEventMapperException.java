package com.jcondotta.application.core.events.mapper.exceptions;

public final class DuplicateDomainEventMapperException extends RuntimeException {

  public static final String DUPLICATE_MAPPER =
    "Multiple DomainEventIntegrationEventMapper implementations registered for domain event type: %s";

  public DuplicateDomainEventMapperException(Class<?> eventType) {
    super(DUPLICATE_MAPPER.formatted(eventType.getName()));
  }
}