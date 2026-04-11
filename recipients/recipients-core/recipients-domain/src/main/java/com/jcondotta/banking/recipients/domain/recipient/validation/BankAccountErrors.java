package com.jcondotta.banking.recipients.domain.recipient.validation;

public final class BankAccountErrors {

  private BankAccountErrors() {
  }

  public static final String ID_MUST_BE_PROVIDED = "id must be provided";
  public static final String ACCOUNT_STATUS_MUST_BE_PROVIDED = "account status must be provided";

  public static final String RECIPIENTS_MUST_NOT_BE_NULL = "recipients must not be null";
}