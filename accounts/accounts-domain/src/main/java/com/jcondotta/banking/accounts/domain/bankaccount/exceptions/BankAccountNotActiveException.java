package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.domain.exception.DomainRuleValidationException;

public final class BankAccountNotActiveException extends DomainRuleValidationException {

  public static final String BANK_ACCOUNT_MUST_BE_ACTIVE = "Bank account must be ACTIVE to perform this operation. Current status: %s";

  public BankAccountNotActiveException(AccountStatus status) {
    super(BANK_ACCOUNT_MUST_BE_ACTIVE.formatted(status));
  }
}