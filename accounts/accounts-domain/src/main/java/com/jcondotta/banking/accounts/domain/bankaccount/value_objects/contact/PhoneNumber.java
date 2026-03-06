package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact;

import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.support.DomainPreconditions;

public record PhoneNumber(String value) {

  public static final String MUST_NOT_BE_EMPTY = "Phone number must not be empty";
  public static final String INVALID_FORMAT = "Phone number must be in valid international format (e.g. +34600111222).";

  private static final String E164_REGEX = "^\\+[1-9]\\d{7,14}$";

  public PhoneNumber {
    DomainPreconditions.requiredNotBlank(value, MUST_NOT_BE_EMPTY);

    value = value.trim();

    if (!value.matches(E164_REGEX)) {
      throw new DomainValidationException(INVALID_FORMAT);
    }
  }

  public static PhoneNumber of(String value) {
    return new PhoneNumber(value);
  }
}