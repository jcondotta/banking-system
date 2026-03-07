package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

public final class BankAccountNotFoundException extends com.jcondotta.domain.exception.DomainValidationException {

  public static final String BANK_ACCOUNT_NOT_FOUND = "Bank account with id '%s' was not found";

  public BankAccountNotFoundException(BankAccountId bankAccountId) {
    super(BANK_ACCOUNT_NOT_FOUND.formatted(bankAccountId));
  }
}