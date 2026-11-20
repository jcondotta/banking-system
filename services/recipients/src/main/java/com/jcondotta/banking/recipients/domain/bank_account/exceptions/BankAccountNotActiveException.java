package com.jcondotta.banking.recipients.domain.bank_account.exceptions;

import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public final class BankAccountNotActiveException extends DomainRuleViolationException {

  public static final String MESSAGE = "Bank account is not active";

  private final BankAccountStatus status;

  public BankAccountNotActiveException(BankAccountStatus status) {
    super(MESSAGE);
    this.status = status;
  }

  public BankAccountStatus getStatus() {
    return status;
  }
}
