package com.jcondotta.banking.recipients.domain.recipient.validation;

public final class RecipientError {

  private RecipientError() {
  }

  public static final String RECIPIENT_ID_NOT_PROVIDED = "recipient id must not be null";
  public static final String RECIPIENT_NAME_NOT_PROVIDED = "recipient name must not be null";
  public static final String IBAN_NOT_PROVIDED = "iban must not be null";
  public static final String STATUS_NOT_PROVIDED = "status must not be null";
  public static final String CREATED_AT_NOT_PROVIDED = "createdAt must not be null";
}