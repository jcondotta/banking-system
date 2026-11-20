package com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions;

public final class BrokerMessageSendException extends RuntimeException {

  public BrokerMessageSendException(String destination, String key, Throwable cause) {
    super("Failed to send broker message. destination=%s, key=%s".formatted(destination, key), cause);
  }
}
