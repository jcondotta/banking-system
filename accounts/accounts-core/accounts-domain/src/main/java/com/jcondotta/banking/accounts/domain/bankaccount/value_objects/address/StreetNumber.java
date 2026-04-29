package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.domain.exception.InvalidDomainDataException;

import static com.jcondotta.domain.support.Preconditions.requiredNotBlank;

public record StreetNumber(String value) {

  public static final String MUST_NOT_BE_EMPTY = "Street number must not be empty";
  public static final String MUST_NOT_EXCEED_LENGTH =
    "Street number must not exceed %d characters";

  public static final int MAX_LENGTH = 15;

  public StreetNumber {
    requiredNotBlank(value, MUST_NOT_BE_EMPTY);

    if (value.length() > MAX_LENGTH) {
      throw new InvalidDomainDataException(
        MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }
  }

  public static StreetNumber of(String value) {
    return new StreetNumber(value);
  }
}
