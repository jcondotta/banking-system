package com.jcondotta.banking.infrastructure.outbox.exceptions;

public class OutboxSerializationException extends RuntimeException {

  public OutboxSerializationException(Class<?> eventClass, Throwable cause) {
    super("Failed to serialize integration event: " + eventClass.getName(), cause);
  }
}
