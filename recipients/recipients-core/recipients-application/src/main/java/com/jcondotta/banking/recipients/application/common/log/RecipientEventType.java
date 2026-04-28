package com.jcondotta.banking.recipients.application.common.log;

public final class RecipientEventType {

  public static final String CREATE        = "recipient.create";
  public static final String CREATE_FAILED = "recipient.create.failed";

  public static final String REMOVE        = "recipient.remove";
  public static final String REMOVE_FAILED = "recipient.remove.failed";

  public static final String LIST          = "recipient.list";
  public static final String LIST_FAILED   = "recipient.list.failed";

  private RecipientEventType() {}
}
