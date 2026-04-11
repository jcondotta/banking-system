package com.jcondotta.banking.recipients.domain.recipient.value_objects;

import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.support.DomainPreconditions;
import org.apache.commons.lang3.StringUtils;

public record RecipientName(String value) {

  static final int MAX_LENGTH = 50;

  static final String NAME_NOT_PROVIDED = "recipient name must be provided";
  static final String NAME_NOT_BLANK = "recipient name must not be blank";

  public static final String NAME_MUST_NOT_EXCEED_LENGTH = "recipient name must not exceed %d characters";

  public RecipientName {
    DomainPreconditions.required(value, NAME_NOT_PROVIDED);

    var normalized = StringUtils.normalizeSpace(value).trim();

    DomainPreconditions.requiredNotBlank(value, NAME_NOT_BLANK);

    if (normalized.length() > MAX_LENGTH) {
      throw new DomainValidationException(
        NAME_MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH)
      );
    }

    value = normalized;
  }

  public static RecipientName of(String value) {
    return new RecipientName(value);
  }
}
