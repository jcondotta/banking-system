package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public final class RecipientNotFoundException extends DomainNotFoundException {

  public static final String MESSAGE = "Recipient not found";

  private final String recipientId;
  private final String bankAccountId;

  public RecipientNotFoundException(RecipientId recipientId, BankAccountId bankAccountId) {
    super(MESSAGE);
    this.recipientId = recipientId.value().toString();
    this.bankAccountId = bankAccountId.value().toString();
  }

  public String getRecipientId() {
    return recipientId;
  }

  public String getBankAccountId() {
    return bankAccountId;
  }
}
