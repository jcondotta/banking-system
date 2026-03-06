package com.jcondotta.banking.recipients.domain.recipient.enums;

public enum AccountStatus {
  ACTIVE,
  CLOSED,
  PENDING,
  BLOCKED,
  UNKNOWN;

  public boolean isActive() {
    return this == ACTIVE;
  }
}
