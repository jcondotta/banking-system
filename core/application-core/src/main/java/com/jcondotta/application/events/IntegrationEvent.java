package com.jcondotta.application.events;

public interface IntegrationEvent<T> {

  String eventType();

  IntegrationEventMetadata metadata();
  T payload();
}