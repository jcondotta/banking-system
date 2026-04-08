package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.domain.exception.DomainRuleValidationException;

public class InvalidBankAccountHoldersConfigurationException extends DomainRuleValidationException {

  public InvalidBankAccountHoldersConfigurationException(int actual, int min, int max) {
    super(
      String.format(
        "BankAccount must have between %d and %d primary account holders. Actual: %d",
        min,
        max,
        actual
      )
    );
  }
}
