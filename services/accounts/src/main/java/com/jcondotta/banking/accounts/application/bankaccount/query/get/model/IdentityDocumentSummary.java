package com.jcondotta.banking.accounts.application.bankaccount.query.get.model;

import static java.util.Objects.requireNonNull;

public record IdentityDocumentSummary(
  String country,
  String type,
  String number
) {

  static final String COUNTRY_REQUIRED = "country must be provided";
  static final String TYPE_REQUIRED = "type must be provided";
  static final String NUMBER_REQUIRED = "number must be provided";

  public IdentityDocumentSummary {
    requireNonNull(country, COUNTRY_REQUIRED);
    requireNonNull(type, TYPE_REQUIRED);
    requireNonNull(number, NUMBER_REQUIRED);
  }
}