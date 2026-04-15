package com.jcondotta.banking.recipients.domain.recipient.enums;

public enum AccountStatus {
  ACTIVE,
  CLOSED,
  PENDING,
  BLOCKED;

  public boolean isActive() {
    return this == ACTIVE;
  }
}
