package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.domain.exception.DomainRuleViolationException;

public class MaxJointHoldersExceededException extends DomainRuleViolationException {

  public MaxJointHoldersExceededException(int maxAllowed) {
    super("Maximum number of joint account holders exceeded. Max allowed: " + maxAllowed);
  }
}