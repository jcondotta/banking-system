package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.banking.accounts.domain.common.FailureReasonProvider;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public class BankAccountNotFoundException extends DomainNotFoundException implements FailureReasonProvider {

  public BankAccountNotFoundException(BankAccountId bankAccountId) {
    super("Bank account not found with id: " + bankAccountId.value());
  }

  @Override
  public FailureReason reason() {
    return FailureReason.NOT_FOUND;
  }
}
