package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.domain.exception.DomainRuleViolationException;

public class CannotDeactivatePrimaryHolderException extends DomainRuleViolationException {

  public static final String PRIMARY_ACCOUNT_HOLDER_CANNOT_BE_DEACTIVATED =
    "Primary account holder cannot be deactivated";

  public CannotDeactivatePrimaryHolderException() {
    super(PRIMARY_ACCOUNT_HOLDER_CANNOT_BE_DEACTIVATED);
  }
}