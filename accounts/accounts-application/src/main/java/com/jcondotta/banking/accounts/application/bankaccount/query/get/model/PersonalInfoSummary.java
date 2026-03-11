package com.jcondotta.banking.accounts.application.bankaccount.query.get.model;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public record PersonalInfoSummary(
  String firstName,
  String lastName,
  IdentityDocumentSummary identityDocument,
  LocalDate dateOfBirth
) {

  static final String FIRST_NAME_REQUIRED = "firstName must be provided";
  static final String LAST_NAME_REQUIRED = "lastName must be provided";
  static final String IDENTITY_DOCUMENT_REQUIRED = "identityDocument must be provided";
  static final String DATE_OF_BIRTH_REQUIRED = "dateOfBirth must be provided";

  public PersonalInfoSummary {
    requireNonNull(firstName, FIRST_NAME_REQUIRED);
    requireNonNull(lastName, LAST_NAME_REQUIRED);
    requireNonNull(identityDocument, IDENTITY_DOCUMENT_REQUIRED);
    requireNonNull(dateOfBirth, DATE_OF_BIRTH_REQUIRED);
  }
}