package com.jcondotta.banking.infrastructure.outbox.log;

public final class OutboxOperation {

  public static final String DISPATCH = "outbox.dispatch";
  public static final String PROCESS  = "outbox.process";
  public static final String PUBLISH  = "outbox.publish";

  private OutboxOperation() {}
}
