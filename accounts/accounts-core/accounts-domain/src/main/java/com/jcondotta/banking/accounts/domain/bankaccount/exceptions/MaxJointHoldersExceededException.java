package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.domain.exception.DomainRuleValidationException;

public class MaxJointHoldersExceededException extends DomainRuleValidationException {

  public MaxJointHoldersExceededException(int maxAllowed) {
    super("Maximum number of joint account holders exceeded. Max allowed: " + maxAllowed);
  }
}