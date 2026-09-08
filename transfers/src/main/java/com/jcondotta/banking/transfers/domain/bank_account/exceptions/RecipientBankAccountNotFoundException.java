package com.jcondotta.banking.transfers.domain.bank_account.exceptions;

import com.jcondotta.banking.transfers.domain.bank_account.value_objects.Iban;
import com.jcondotta.domain.exception.DomainNotFoundException;

public final class RecipientBankAccountNotFoundException extends DomainNotFoundException {

  public static final String MESSAGE = "Recipient bank account not found";

  private final String maskedIban;

  public RecipientBankAccountNotFoundException(Iban iban) {
    super(MESSAGE);
    this.maskedIban = iban.masked();
  }

  public String getMaskedIban() {
    return maskedIban;
  }
}
