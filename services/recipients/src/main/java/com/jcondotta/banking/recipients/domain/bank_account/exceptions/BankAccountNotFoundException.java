package com.jcondotta.banking.recipients.domain.bank_account.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public final class BankAccountNotFoundException extends DomainNotFoundException {

  public static final String MESSAGE = "Bank account not found";

  private final BankAccountId bankAccountId;

  public BankAccountNotFoundException(BankAccountId bankAccountId) {
    super(MESSAGE);
    this.bankAccountId = bankAccountId;
  }

  public BankAccountId getBankAccountId() {
    return bankAccountId;
  }
}
