package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.banking.accounts.domain.common.FailureReasonProvider;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public class AccountHolderNotFoundException extends DomainNotFoundException implements FailureReasonProvider {

  public AccountHolderNotFoundException(AccountHolderId accountHolderId) {
    super("Account holder not found with id: " + accountHolderId.value());
  }

  @Override
  public FailureReason reason() {
    return FailureReason.ACCOUNT_HOLDER_NOT_FOUND;
  }
}
