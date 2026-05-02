package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;

public final class DuplicateRecipientIbanException extends RecipientDomainConflictException {

  public static final String MESSAGE = "Recipient IBAN already exists";

  private final String maskedIban;
  private final String bankAccountId;

  public DuplicateRecipientIbanException(Iban iban, BankAccountId bankAccountId) {
    super(MESSAGE);
    this.maskedIban = iban.masked();
    this.bankAccountId = bankAccountId.value().toString();
  }

  @Override
  public FailureReason reason() {
    return FailureReason.DUPLICATE_IBAN;
  }

  public String getMaskedIban() {
    return maskedIban;
  }

  public String getBankAccountId() {
    return bankAccountId;
  }
}
