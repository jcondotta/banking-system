package com.jcondotta.banking.accounts.domain.common;

import com.jcondotta.domain.exception.DomainException;

import java.util.Locale;

public enum FailureReason {
  NOT_FOUND,
  NOT_ACTIVE,
  INVALID_STATE_TRANSITION,
  MAX_JOINT_HOLDERS_EXCEEDED,
  ACCOUNT_HOLDER_NOT_FOUND,
  CANNOT_DEACTIVATE_PRIMARY_HOLDER,
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
