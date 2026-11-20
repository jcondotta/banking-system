package com.jcondotta.banking.accounts.application.common.exception;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainConflictException;

import java.util.UUID;

public final class BankAccountOptimisticLockException extends DomainConflictException {

  public static final String BANK_ACCOUNT_CONCURRENT_MODIFICATION =
    "Bank account was modified concurrently - please reload and retry";

  private final UUID bankAccountId;

  public BankAccountOptimisticLockException(BankAccountId id) {
    super(BANK_ACCOUNT_CONCURRENT_MODIFICATION);
    this.bankAccountId = id.value();
  }

  public UUID getBankAccountId() {
    return bankAccountId;
  }
}
