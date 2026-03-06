package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal;

import static com.jcondotta.domain.support.DomainPreconditions.required;

public record PersonalInfo(
  AccountHolderName holderName,
  IdentityDocument identityDocument,
  DateOfBirth dateOfBirth
) {

  public static final String NAME_NOT_PROVIDED = "Account holder holderName must be provided.";
  public static final String IDENTITY_DOCUMENT_NOT_PROVIDED = "Identity document must be provided.";
  public static final String DATE_OF_BIRTH_NOT_PROVIDED = "Date of birth must be provided.";

  public PersonalInfo {
    required(holderName, NAME_NOT_PROVIDED);
    required(identityDocument, IDENTITY_DOCUMENT_NOT_PROVIDED);
    required(dateOfBirth, DATE_OF_BIRTH_NOT_PROVIDED);
  }

  public static PersonalInfo of(AccountHolderName name, IdentityDocument identityDocument, DateOfBirth dateOfBirth) {
    return new PersonalInfo(name, identityDocument, dateOfBirth);
  }
}