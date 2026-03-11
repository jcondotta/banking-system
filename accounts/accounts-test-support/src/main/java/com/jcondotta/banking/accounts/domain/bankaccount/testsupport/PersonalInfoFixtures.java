package com.jcondotta.banking.accounts.domain.bankaccount.testsupport;

import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.AccountHolderName;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DateOfBirth;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.IdentityDocument;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.PersonalInfo;

import java.time.LocalDate;
import java.time.Month;

public enum PersonalInfoFixtures {

  JEFFERSON(
    "Jefferson",
    "Condotta",
    IdentityDocumentFixtures.SPANISH_NIE.identityDocument(),
    LocalDate.of(1988, Month.JUNE, 24)
  ),

  VIRGINIO(
    "Virginio",
    "Condotta",
    IdentityDocumentFixtures.SPANISH_DNI.identityDocument(),
    LocalDate.of(1917, Month.DECEMBER, 11)
  ),

  PATRIZIO(
    "Patrizio",
    "Condotta",
    IdentityDocumentFixtures.SPANISH_NIE_2.identityDocument(),
    LocalDate.of(1889, Month.FEBRUARY, 18)
  );

  private final String firstName;
  private final String lastName;
  private final IdentityDocument identityDocument;
  private final LocalDate dateOfBirth;

  PersonalInfoFixtures(
      String firstName,
      String lastName,
      IdentityDocument identityDocument,
      LocalDate dateOfBirth
  ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.identityDocument = identityDocument;
    this.dateOfBirth = dateOfBirth;
  }

  public PersonalInfo personalInfo() {
    return PersonalInfo.of(
        AccountHolderName.of(firstName, lastName),
        identityDocument,
        DateOfBirth.of(dateOfBirth)
    );
  }

  public AccountHolderName accountHolderName() {
    return AccountHolderName.of(firstName, lastName);
  }

  public IdentityDocument identityDocument() {
    return identityDocument;
  }

  public DateOfBirth dateOfBirth() {
    return DateOfBirth.of(dateOfBirth);
  }
}