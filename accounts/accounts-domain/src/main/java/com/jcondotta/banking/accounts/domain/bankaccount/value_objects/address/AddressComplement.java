package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.domain.exception.DomainValidationException;

import static com.jcondotta.domain.support.DomainPreconditions.requiredNotBlank;

public record AddressComplement(String value) {

  public static final String MUST_NOT_BE_EMPTY = "Address complement must not be empty";
  public static final String MUST_NOT_EXCEED_LENGTH = "Address complement must not exceed %d characters";

  public static final int MAX_LENGTH = 40;

  public AddressComplement {
    requiredNotBlank(value, MUST_NOT_BE_EMPTY);

    if (value.length() > MAX_LENGTH) {
      throw new DomainValidationException(
        MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }
  }

  public static AddressComplement of(String value) {
    return new AddressComplement(value);
  }
}