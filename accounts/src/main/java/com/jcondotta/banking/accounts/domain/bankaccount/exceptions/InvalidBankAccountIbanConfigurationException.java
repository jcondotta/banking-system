package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public final class InvalidBankAccountIbanConfigurationException extends DomainRuleViolationException {

  public InvalidBankAccountIbanConfigurationException(AccountStatus accountStatus) {
    super(
      accountStatus.isPending()
        ? "Bank account with PENDING status must not have an IBAN"
        : "Bank account with " + accountStatus + " status must have an IBAN"
    );
  }
}
