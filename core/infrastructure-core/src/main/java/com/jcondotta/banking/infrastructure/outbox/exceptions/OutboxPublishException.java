package com.jcondotta.banking.infrastructure.outbox.exceptions;

public class OutboxPublishException extends RuntimeException {

  public OutboxPublishException(String message, Throwable cause) {
    super(message, cause);
  }
}
