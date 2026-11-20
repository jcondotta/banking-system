package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

public class RecipientEventPublishException extends RuntimeException {

  public RecipientEventPublishException(String eventType, Throwable cause) {
    super("Failed to publish recipient event: " + eventType, cause);
  }
}
