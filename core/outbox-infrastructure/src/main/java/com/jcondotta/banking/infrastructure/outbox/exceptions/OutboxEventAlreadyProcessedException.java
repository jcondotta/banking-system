package com.jcondotta.banking.infrastructure.outbox.exceptions;

public class OutboxEventAlreadyProcessedException extends RuntimeException {

  public OutboxEventAlreadyProcessedException(Object eventId) {
    super("outbox event already processed: " + eventId);
  }

  public OutboxEventAlreadyProcessedException(Object eventId, Throwable cause) {
    super("outbox event already processed: " + eventId, cause);
  }
}
