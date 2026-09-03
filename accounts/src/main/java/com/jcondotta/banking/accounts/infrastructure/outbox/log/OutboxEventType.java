package com.jcondotta.banking.accounts.infrastructure.outbox.log;

public final class OutboxEventType {

  public static final String DISPATCH = "accounts.outbox.dispatch";
  public static final String PROCESS = "accounts.outbox.process";
  public static final String PUBLISH = "accounts.outbox.publish";

  private OutboxEventType() {}
}
