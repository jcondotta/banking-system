package com.jcondotta.banking.accounts.application.bankaccount.query.get.model;

import static java.util.Objects.requireNonNull;

public record ContactInfoSummary(
  String email,
  String phoneNumber
) {

  static final String EMAIL_REQUIRED = "email must be provided";
  static final String PHONE_NUMBER_REQUIRED = "phoneNumber must be provided";

  public ContactInfoSummary {
    requireNonNull(email, EMAIL_REQUIRED);
    requireNonNull(phoneNumber, PHONE_NUMBER_REQUIRED);
  }
}