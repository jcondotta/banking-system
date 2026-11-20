package com.jcondotta.banking.accounts.domain.testsupport;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.IdentityDocument;

public enum IdentityDocumentFixtures {

  SPANISH_DNI(
    DocumentCountry.SPAIN,
    DocumentType.NATIONAL_ID,
    "12345678Z"
  ),

  SPANISH_NIE(
    DocumentCountry.SPAIN,
    DocumentType.FOREIGNER_ID,
    "X7566995H"
  ),

  SPANISH_NIE_2(
    DocumentCountry.SPAIN,
    DocumentType.FOREIGNER_ID,
    "X9999999Z"
  );

  private final DocumentCountry country;
  private final DocumentType type;
  private final String documentNumber;

  IdentityDocumentFixtures(
    DocumentCountry country,
    DocumentType type,
    String documentNumber
  ) {
    this.country = country;
    this.type = type;
    this.documentNumber = documentNumber;
  }

  public IdentityDocument identityDocument() {
    return IdentityDocument.of(
      country,
      type,
      DocumentNumber.of(documentNumber)
    );
  }

  public DocumentCountry country() {
    return country;
  }

  public DocumentType documentType() {
    return type;
  }

  public DocumentNumber documentNumber() {
    return DocumentNumber.of(documentNumber);
  }
}