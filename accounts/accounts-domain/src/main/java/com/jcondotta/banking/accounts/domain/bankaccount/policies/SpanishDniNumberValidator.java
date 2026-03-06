package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;
import com.jcondotta.domain.exception.DomainValidationException;

public final class SpanishDniNumberValidator implements DocumentNumberValidator {

  public static final String DNI_NUMBER_INVALID_FORMAT =
    "Document number is invalid for Spanish DNI.";

  private static final String DNI_REGEX = "^[0-9]{8}[A-Z]$";

  @Override
  public DocumentCountry supportedCountry() {
    return DocumentCountry.SPAIN;
  }

  @Override
  public DocumentType supportedType() {
    return DocumentType.NATIONAL_ID;
  }

  @Override
  public void validate(DocumentNumber documentNumber) {
    if (!documentNumber.value().matches(DNI_REGEX)) {
      throw new DomainValidationException(DNI_NUMBER_INVALID_FORMAT);
    }
  }
}