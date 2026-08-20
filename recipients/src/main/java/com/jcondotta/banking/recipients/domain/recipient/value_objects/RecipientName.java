package com.jcondotta.banking.recipients.domain.recipient.value_objects;

import com.jcondotta.domain.exception.InvalidDomainDataException;

import static com.jcondotta.domain.support.Preconditions.required;
import static com.jcondotta.domain.support.Preconditions.requiredNotBlank;

public record RecipientName(String value) {

  public static final int MAX_LENGTH = 50;

  public static final String NAME_NOT_PROVIDED = "recipient name must be provided";
  public static final String NAME_NOT_BLANK = "recipient name must not be blank";
  public static final String NAME_MUST_NOT_EXCEED_LENGTH = "recipient name must not exceed %d characters";

  public RecipientName {
    required(value, NAME_NOT_PROVIDED);
    value = normalize(value);

    requiredNotBlank(value, NAME_NOT_BLANK);
    validateLength(value);
  }

  private static String normalize(String value) {
    return value.trim().replaceAll("\\s+", " ");
  }

  private static void validateLength(String value) {
    if (value.length() > MAX_LENGTH) {
      throw new InvalidDomainDataException(
        NAME_MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }
  }

  public static RecipientName of(String value) {
    return new RecipientName(value);
  }
}
