package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.banking.accounts.domain.common.FailureReasonProvider;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public final class InvalidBankAccountStateTransitionException extends DomainRuleViolationException implements FailureReasonProvider {

  public InvalidBankAccountStateTransitionException(AccountStatus from,AccountStatus to) {
    super("Cannot transition bank account from " + from + " to " + to);
  }

  @Override
  public FailureReason reason() {
    return FailureReason.INVALID_STATE_TRANSITION;
  }
}
