package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public class AccountHolderNotFoundException extends DomainNotFoundException {

  public AccountHolderNotFoundException(AccountHolderId accountHolderId) {
    super("Account holder not found with id: " + accountHolderId.value());
  }
}