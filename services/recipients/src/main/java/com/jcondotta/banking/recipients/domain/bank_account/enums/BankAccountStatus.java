package com.jcondotta.banking.recipients.domain.bank_account.enums;

public enum BankAccountStatus {
  PENDING,
  ACTIVE,
  BLOCKED,
  CLOSED;

  public boolean isActive() {
    return this == ACTIVE;
  }
}
