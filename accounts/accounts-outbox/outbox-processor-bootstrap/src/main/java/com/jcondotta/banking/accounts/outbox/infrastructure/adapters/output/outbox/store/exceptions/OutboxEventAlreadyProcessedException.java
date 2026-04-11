package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store.exceptions;

public class OutboxEventAlreadyProcessedException extends RuntimeException {

  public OutboxEventAlreadyProcessedException(String eventId) {
    super("outbox event already processed: " + eventId);
  }
}
