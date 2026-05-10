package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.banking.accounts.domain.common.FailureReasonProvider;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public final class BankAccountNotActiveException extends DomainRuleViolationException implements FailureReasonProvider {

  public static final String BANK_ACCOUNT_MUST_BE_ACTIVE = "Bank account must be ACTIVE to perform this operation. Current status: %s";

  public BankAccountNotActiveException(AccountStatus status) {
    super(BANK_ACCOUNT_MUST_BE_ACTIVE.formatted(status));
  }

  @Override
  public FailureReason reason() {
    return FailureReason.NOT_ACTIVE;
  }
}
