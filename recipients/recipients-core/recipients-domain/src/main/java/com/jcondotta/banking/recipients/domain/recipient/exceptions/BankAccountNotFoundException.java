package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public final class BankAccountNotFoundException extends DomainNotFoundException {

  public static final String BANK_ACCOUNT_NOT_FOUND = "Bank account with id '%s' was not found";

  public BankAccountNotFoundException(BankAccountId bankAccountId) {
    super(BANK_ACCOUNT_NOT_FOUND.formatted(bankAccountId.value()));
  }
}