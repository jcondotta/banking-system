package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

public class OutboxEventAlreadyProcessedException extends RuntimeException {

  public OutboxEventAlreadyProcessedException(String eventId) {
    super("outbox event already processed: " + eventId);
  }
}