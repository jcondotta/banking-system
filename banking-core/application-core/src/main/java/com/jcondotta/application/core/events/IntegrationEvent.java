package com.jcondotta.application.core.events;

public interface IntegrationEvent<T> {

  String eventType();

  IntegrationEventMetadata metadata();
  T payload();
}