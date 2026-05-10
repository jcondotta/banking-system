package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact;

import com.jcondotta.domain.exception.InvalidDomainDataException;
import com.jcondotta.domain.support.Preconditions;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {

  public static final int MAX_LENGTH = 100;

  public static final String MUST_NOT_BE_EMPTY = "Email must not be empty";
  public static final String INVALID_FORMAT = "Email format is invalid";
  public static final String MUST_NOT_EXCEED_LENGTH =
    "Email must not exceed %d characters";

  private static final Pattern EMAIL_REGEX =
    Pattern.compile(
      "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
      Pattern.CASE_INSENSITIVE
    );

  public Email {
    Preconditions.requiredNotBlank(value, MUST_NOT_BE_EMPTY);

    value = value.trim().toLowerCase(Locale.ROOT);

    if (value.length() > MAX_LENGTH) {
      throw new InvalidDomainDataException(
        MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }

    if (!EMAIL_REGEX.matcher(value).matches()) {
      throw new InvalidDomainDataException(INVALID_FORMAT);
    }
  }

  public static Email of(String value) {
    return new Email(value);
  }
}
