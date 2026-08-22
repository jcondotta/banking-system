package com.jcondotta.banking.recipients.application.common.log;

public final class RecipientEventType {

  public static final String CREATE = "recipients.create";
  public static final String REMOVE = "recipients.remove";
  public static final String LIST = "recipients.list";
  public static final String GET = "recipients.get";

  private RecipientEventType() {}
}
