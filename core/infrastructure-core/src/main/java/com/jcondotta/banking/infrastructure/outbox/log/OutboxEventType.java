package com.jcondotta.banking.infrastructure.outbox.log;

public final class OutboxEventType {

  public static final String PUBLISHED = "outbox.published";
  public static final String PUBLISH_FAILED = "outbox.publish_failed";
  public static final String CLAIM_FAILED = "outbox.claim_failed";
  public static final String MAX_RETRIES_EXCEEDED = "outbox.max_retries_exceeded";

  private OutboxEventType() {}
}
