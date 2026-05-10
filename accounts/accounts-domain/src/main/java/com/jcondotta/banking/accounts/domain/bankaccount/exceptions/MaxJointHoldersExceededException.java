package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.banking.accounts.domain.common.FailureReasonProvider;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public class MaxJointHoldersExceededException extends DomainRuleViolationException implements FailureReasonProvider {

  public MaxJointHoldersExceededException(int maxAllowed) {
    super("Maximum number of joint account holders exceeded. Max allowed: " + maxAllowed);
  }

  @Override
  public FailureReason reason() {
    return FailureReason.MAX_JOINT_HOLDERS_EXCEEDED;
  }
}
