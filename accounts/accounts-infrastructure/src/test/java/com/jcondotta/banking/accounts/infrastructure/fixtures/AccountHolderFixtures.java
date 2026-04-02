package com.jcondotta.banking.accounts.infrastructure.fixtures;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.Email;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.AccountHolderName;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DateOfBirth;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.IdentityDocument;

import java.time.LocalDate;
import java.time.Month;

@Deprecated
public enum AccountHolderFixtures {

//  JEFFERSON(
//    "Jefferson Condotta",
//    DocumentType.PASSPORT,
//    "FH254787",
//    LocalDate.of(1988, Month.JUNE, 24),
//    "jefferson.condotta@email.com"
//  ),
//
//  VIRGINIO(
//    "Virginio Condotta",
//    DocumentType.PASSPORT,
//    "BC858683",
//    LocalDate.of(1917, Month.DECEMBER, 11),
//    "virginio.condotta@email.com"
//  ),
//
//  PATRIZIO(
//    "Patrizio Condotta",
//    DocumentType.NATIONAL_ID,
//    "12345678Z",
//    LocalDate.of(1889, Month.FEBRUARY, 18),
//    "patrizio.condotta@email.com"
//  );
//
//  AccountHolderFixtures(
//    String accountHolderName,
//    DocumentType documentType,
//    String documentNumber,
//    LocalDate dateOfBirth,
//    String email
//  ) {
//    this.accountHolderName = accountHolderName;
//    this.documentType = documentType;
//    this.documentNumber = documentNumber;
//    this.dateOfBirth = dateOfBirth;
//    this.email = email;
//  }
//
//  private final String accountHolderName;
//  private final DocumentType documentType;
//  private final String documentNumber;
//  private final LocalDate dateOfBirth;
//  private final String email;
//
//  public AccountHolderName getAccountHolderName() {
//    return AccountHolderName.of(accountHolderName);
//  }
//
//  public DateOfBirth getDateOfBirth() {
//    return DateOfBirth.of(dateOfBirth);
//  }
//
//  public IdentityDocument getIdentityDocument() {
////    return new IdentityDocument(documentType, DocumentNumber.of(documentNumber));
//    return null;
//  }
//
//  public Email getEmail() {
//    return Email.of(email);
//  }
}