package com.jcondotta.banking.recipients.domain.bank_account;

import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccount(BankAccountId id, BankAccountStatus status) {

  public static final String ID_NOT_PROVIDED = "bank account id must be provided";
  public static final String STATUS_NOT_PROVIDED = "bank account status must be provided";

  public BankAccount {
    required(id, ID_NOT_PROVIDED);
    required(status, STATUS_NOT_PROVIDED);
  }

  public static BankAccount active(BankAccountId id) {
    return new BankAccount(id, BankAccountStatus.ACTIVE);
  }

  public boolean isActive() {
    return status.isActive();
  }
}
