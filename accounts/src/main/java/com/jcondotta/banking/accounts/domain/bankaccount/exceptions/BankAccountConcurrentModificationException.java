package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainConflictException;

public final class BankAccountConcurrentModificationException extends DomainConflictException {

  public BankAccountConcurrentModificationException(BankAccountId id) {
    super("Concurrent modification detected for bank account: " + id.value());
  }
}
