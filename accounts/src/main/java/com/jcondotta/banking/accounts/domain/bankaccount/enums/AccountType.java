package com.jcondotta.banking.accounts.domain.bankaccount.enums;

public enum AccountType {
  SAVINGS, CHECKING;

  public boolean isSavings() {
    return this == SAVINGS;
  }

  public boolean isChecking() {
    return this == CHECKING;
  }
}
