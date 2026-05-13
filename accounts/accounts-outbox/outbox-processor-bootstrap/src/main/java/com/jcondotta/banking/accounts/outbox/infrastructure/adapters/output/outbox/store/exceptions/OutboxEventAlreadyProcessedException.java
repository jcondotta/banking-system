package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store.exceptions;

public class OutboxEventAlreadyProcessedException extends RuntimeException {

  public OutboxEventAlreadyProcessedException(Object eventId) {
    super("outbox event already processed: " + eventId);
  }

  public OutboxEventAlreadyProcessedException(Object eventId, Throwable cause) {
    super("outbox event already processed: " + eventId, cause);
  }
}
