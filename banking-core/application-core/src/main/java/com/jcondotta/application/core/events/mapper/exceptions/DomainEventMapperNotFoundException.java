package com.jcondotta.application.core.events.mapper.exceptions;

public final class DomainEventMapperNotFoundException extends RuntimeException {

  public static final String MAPPER_NOT_FOUND =
    "No DomainEventIntegrationEventMapper registered for domain event type: %s";

  public DomainEventMapperNotFoundException(Class<?> eventType) {
    super(MAPPER_NOT_FOUND.formatted(eventType.getName()));
  }
}