package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox;

public class OutboxPublishException extends RuntimeException {
  public OutboxPublishException(String message, Throwable cause) {
    super(message, cause);
  }
}