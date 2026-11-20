package com.jcondotta.banking.infrastructure.adapters.output.messaging;

public record EventRouting(String destination, String key) {

  public EventRouting {
    if (destination == null || destination.isBlank()) {
      throw new IllegalArgumentException("destination must not be blank");
    }
    if (key == null || key.isBlank()) {
      throw new IllegalArgumentException("key must not be blank");
    }
  }
}
