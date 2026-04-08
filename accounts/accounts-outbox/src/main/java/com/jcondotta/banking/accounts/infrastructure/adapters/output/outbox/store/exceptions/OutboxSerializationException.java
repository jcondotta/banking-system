package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.exceptions;

public class OutboxSerializationException extends RuntimeException {

  public OutboxSerializationException(Class<?> eventClass, Throwable cause) {
    super("Failed to serialize integration event: " + eventClass.getName(), cause);
  }
}