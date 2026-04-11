package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox;

public class OutboxPublishException extends RuntimeException {
  public OutboxPublishException(String message, Throwable cause) {
    super(message, cause);
  }
}
