package com.jcondotta.banking.transfers.domain.bank_account.exceptions;

import com.jcondotta.banking.transfers.domain.bank_account.value_objects.Iban;
import com.jcondotta.banking.transfers.domain.common.FailureReason;
import com.jcondotta.banking.transfers.domain.common.FailureReasonProvider;
import com.jcondotta.domain.exception.DomainNotFoundException;

public final class RecipientBankAccountNotFoundException extends DomainNotFoundException implements FailureReasonProvider {

  public static final String MESSAGE = "Recipient bank account not found";

  private final String maskedIban;

  public RecipientBankAccountNotFoundException(Iban iban) {
    super(MESSAGE);
    this.maskedIban = iban.masked();
  }

  @Override
  public FailureReason reason() {
    return FailureReason.NOT_FOUND;
  }

  public String getMaskedIban() {
    return maskedIban;
  }
}
