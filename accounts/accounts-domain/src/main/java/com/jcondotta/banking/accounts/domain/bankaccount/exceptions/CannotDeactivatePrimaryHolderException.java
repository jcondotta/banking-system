package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.banking.accounts.domain.common.FailureReasonProvider;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public class CannotDeactivatePrimaryHolderException extends DomainRuleViolationException implements FailureReasonProvider {

  public static final String PRIMARY_ACCOUNT_HOLDER_CANNOT_BE_DEACTIVATED =
    "Primary account holder cannot be deactivated";

  public CannotDeactivatePrimaryHolderException() {
    super(PRIMARY_ACCOUNT_HOLDER_CANNOT_BE_DEACTIVATED);
  }

  @Override
  public FailureReason reason() {
    return FailureReason.CANNOT_DEACTIVATE_PRIMARY_HOLDER;
  }
}
