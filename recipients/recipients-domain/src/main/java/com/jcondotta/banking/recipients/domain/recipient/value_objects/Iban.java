package com.jcondotta.banking.recipients.domain.recipient.value_objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

import java.util.Locale;
import java.util.Objects;

public record Iban(String value) {

  public static final String IBAN_NOT_PROVIDED = "IBAN must be provided.";
  public static final String IBAN_INVALID_FORMAT_MESSAGE = "IBAN format is invalid.";

  public Iban {
    Objects.requireNonNull(value, IBAN_NOT_PROVIDED);

    var sanitizedValue = StringUtils.deleteWhitespace(value).toUpperCase(Locale.ROOT);
    if (!IBANCheckDigit.IBAN_CHECK_DIGIT.isValid(sanitizedValue)) {
      throw new IllegalArgumentException(IBAN_INVALID_FORMAT_MESSAGE);
    }

    value = sanitizedValue;
  }

  public static Iban of(String value) {
    return new Iban(value);
  }
}
