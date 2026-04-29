package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.domain.exception.InvalidDomainDataException;

import static com.jcondotta.domain.support.Preconditions.requiredNotBlank;

public record City(String value) {

  public static final String MUST_NOT_BE_EMPTY = "City must not be empty";
  public static final String MUST_NOT_EXCEED_LENGTH =
    "City must not exceed %d characters";

  public static final int MAX_LENGTH = 100;

  public City {
    requiredNotBlank(value, MUST_NOT_BE_EMPTY);

    if (value.length() > MAX_LENGTH) {
      throw new InvalidDomainDataException(
        MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }
  }

  public static City of(String value) {
    return new City(value);
  }
}
