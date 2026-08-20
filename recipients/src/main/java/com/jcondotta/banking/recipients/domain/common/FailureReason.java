package com.jcondotta.banking.recipients.domain.common;

import com.jcondotta.domain.exception.DomainException;

import java.util.Locale;

public enum FailureReason {
  DUPLICATE_IBAN,
  NOT_FOUND,
  ALREADY_EXISTS,
  OWNERSHIP_MISMATCH,
  OPTIMISTIC_LOCK_CONFLICT,
  DOMAIN_ERROR,
  INTERNAL_ERROR;

  public String normalize() {
    return this.name().toLowerCase(Locale.ROOT);
  }

  public static FailureReason from(DomainException ex) {
    if (ex instanceof FailureReasonProvider provider) {
      return provider.reason();
    }
    return DOMAIN_ERROR;
  }
}
