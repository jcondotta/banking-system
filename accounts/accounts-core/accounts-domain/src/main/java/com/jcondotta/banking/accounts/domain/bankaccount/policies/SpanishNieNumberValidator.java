package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;
import com.jcondotta.domain.exception.DomainValidationException;

public final class SpanishNieNumberValidator implements DocumentNumberValidator {

  public static final String NIE_NUMBER_INVALID_FORMAT =
    "Document number is invalid for Spanish NIE.";

  private static final String NIE_REGEX = "^[XYZ][0-9]{7}[A-Z]$";

  @Override
  public DocumentCountry supportedCountry() {
    return DocumentCountry.SPAIN;
  }

  @Override
  public DocumentType supportedType() {
    return DocumentType.FOREIGNER_ID;
  }

  @Override
  public void validate(DocumentNumber documentNumber) {
    if (!documentNumber.value().matches(NIE_REGEX)) {
      throw new DomainValidationException(NIE_NUMBER_INVALID_FORMAT);
    }
  }
}