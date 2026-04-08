package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public class BankAccountNotFoundException extends DomainNotFoundException {

  public BankAccountNotFoundException(BankAccountId bankAccountId) {
    super("Bank account not found with id: " + bankAccountId.value());
  }
}