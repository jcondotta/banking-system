package com.jcondotta.banking.accounts.domain.bankaccount.value_objects;

import com.jcondotta.domain.exception.DomainValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

import java.util.Locale;

public record Iban(String value) {

  public static final String IBAN_NOT_PROVIDED = "IBAN must be provided.";
  public static final String IBAN_INVALID_FORMAT = "IBAN format is invalid.";

  public Iban {
    if (value == null) {
      throw new DomainValidationException(IBAN_NOT_PROVIDED);
    }

    var sanitized = StringUtils.deleteWhitespace(value).toUpperCase(Locale.ROOT);

    if (sanitized.isEmpty()) {
      throw new DomainValidationException(IBAN_NOT_PROVIDED);
    }

    if (!IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(sanitized)) {
      throw new DomainValidationException(IBAN_INVALID_FORMAT);
    }

    value = sanitized;
  }

  public static Iban of(String value) {
    return new Iban(value);
  }
}