package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal;

import com.jcondotta.domain.exception.DomainValidationException;

import static com.jcondotta.domain.support.DomainPreconditions.requiredNotBlank;

public record DocumentNumber(String value) {

  public static final int MAX_LENGTH = 40;

  public static final String MUST_NOT_BE_EMPTY = "Document number must not be empty";
  public static final String MUST_NOT_EXCEED_LENGTH =
    "Document number must not exceed %d characters";

  public DocumentNumber {
    requiredNotBlank(value, MUST_NOT_BE_EMPTY);

    value = value.trim();

    if (value.length() > MAX_LENGTH) {
      throw new DomainValidationException(
        MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }
  }

  public static DocumentNumber of(String value) {
    return new DocumentNumber(value);
  }
}