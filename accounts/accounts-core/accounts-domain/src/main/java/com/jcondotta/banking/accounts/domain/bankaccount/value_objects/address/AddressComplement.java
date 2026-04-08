package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.domain.exception.DomainValidationException;

import static com.jcondotta.domain.support.DomainPreconditions.requiredNotBlank;

public record AddressComplement(String value) {

  public static final String MUST_NOT_BE_EMPTY = "addressComplement must not be empty";
  public static final String MUST_NOT_EXCEED_LENGTH = "addressComplement must not exceed %d characters";

  public static final int MAX_LENGTH = 40;

  public AddressComplement {
    requiredNotBlank(value, MUST_NOT_BE_EMPTY);

    if (value.length() > MAX_LENGTH) {
      throw new DomainValidationException(
        MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }
  }

  public static AddressComplement ofNullable(String value) {
    if (value == null || value.isBlank()) return null;
    return new AddressComplement(value);
  }
}