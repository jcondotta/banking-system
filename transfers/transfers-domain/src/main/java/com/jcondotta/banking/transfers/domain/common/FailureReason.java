package com.jcondotta.banking.transfers.domain.common;

import com.jcondotta.domain.exception.DomainException;

import java.util.Locale;

public enum FailureReason {
  NOT_FOUND,
  DOMAIN_ERROR,
  INTERNAL_ERROR;

  public String normalize() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static FailureReason from(DomainException ex) {
    if (ex instanceof FailureReasonProvider provider) {
      return provider.reason();
    }

    return DOMAIN_ERROR;
  }
}
