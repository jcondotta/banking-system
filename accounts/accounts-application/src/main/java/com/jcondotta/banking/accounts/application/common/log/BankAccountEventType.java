package com.jcondotta.banking.accounts.application.common.log;

public final class BankAccountEventType {

  public static final String OPEN = "accounts.open";
  public static final String ACTIVATE = "accounts.activate";
  public static final String BLOCK = "accounts.block";
  public static final String UNBLOCK = "accounts.unblock";
  public static final String CLOSE = "accounts.close";
  public static final String ADD_JOINT_HOLDER = "accounts.addJointHolder";
  public static final String GET_BY_ID = "accounts.getById";

  private BankAccountEventType() {}
}
