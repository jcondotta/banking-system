package com.jcondotta.banking.infrastructure.adapters.output.messaging;

public record EventPublication(EventEnvelope envelope, EventRouting routing) {

  public EventPublication {
    if (envelope == null) {
      throw new IllegalArgumentException("envelope must not be null");
    }
    if (routing == null) {
      throw new IllegalArgumentException("routing must not be null");
    }
  }
}
