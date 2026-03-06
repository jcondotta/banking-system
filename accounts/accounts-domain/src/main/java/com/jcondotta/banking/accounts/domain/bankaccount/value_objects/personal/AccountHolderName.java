package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal;

import com.jcondotta.domain.exception.DomainValidationException;

import static com.jcondotta.domain.support.DomainPreconditions.requiredNotBlank;

public record AccountHolderName(String firstName, String lastName) {

  public static final int MAX_LENGTH = 255;

  public static final String FIRST_NAME_MUST_NOT_BE_EMPTY = "First holderName must not be empty";
  public static final String LAST_NAME_MUST_NOT_BE_EMPTY = "Last holderName must not be empty";

  public static final String FIRST_NAME_MUST_NOT_EXCEED_LENGTH = "First holderName must not exceed %d characters";
  public static final String LAST_NAME_MUST_NOT_EXCEED_LENGTH = "Last holderName must not exceed %d characters";

  public AccountHolderName {
    firstName = normalize(firstName);
    lastName = normalize(lastName);

    requiredNotBlank(firstName, FIRST_NAME_MUST_NOT_BE_EMPTY);
    requiredNotBlank(lastName, LAST_NAME_MUST_NOT_BE_EMPTY);

    if (firstName.length() > MAX_LENGTH) {
      throw new DomainValidationException(
        FIRST_NAME_MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }

    if (lastName.length() > MAX_LENGTH) {
      throw new DomainValidationException(
        LAST_NAME_MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }
  }

  private static String normalize(String value) {
    if (value == null) return null;
    return value.trim().replaceAll("\\s+", " ");
  }

  public static AccountHolderName of(String firstName, String lastName) {
    return new AccountHolderName(firstName, lastName);
  }

  public String fullName() {
    return firstName + " " + lastName;
  }
}