package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import static com.jcondotta.domain.support.Preconditions.checkArgument;
import static com.jcondotta.domain.support.Preconditions.requiredNotBlank;

public record AddressComplement(String value) {

  public static final String MUST_NOT_BE_EMPTY = "addressComplement must not be empty";
  public static final String MUST_NOT_EXCEED_LENGTH = "addressComplement must not exceed %d characters";

  public static final int MAX_LENGTH = 40;

  public AddressComplement {
    requiredNotBlank(value, MUST_NOT_BE_EMPTY);
    checkArgument(value.length() <= MAX_LENGTH, MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH));
  }

  public static AddressComplement ofNullable(String value) {
    if (value == null || value.isBlank()) return null;
    return new AddressComplement(value);
  }
}
