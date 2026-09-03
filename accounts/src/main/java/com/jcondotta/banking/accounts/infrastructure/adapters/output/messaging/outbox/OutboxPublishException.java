package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

public class OutboxPublishException extends RuntimeException {
  public OutboxPublishException(String message, Throwable cause) {
    super(message, cause);
  }
}
