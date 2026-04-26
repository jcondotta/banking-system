package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.exception.DomainRuleValidationException;

public final class RecipientOwnershipMismatchException extends DomainRuleValidationException {

  public static final String MESSAGE = "Recipient does not belong to the informed bank account";

  private final String recipientId;
  private final String bankAccountId;

  public RecipientOwnershipMismatchException(RecipientId recipientId, BankAccountId bankAccountId) {
    super(MESSAGE);
    this.recipientId = recipientId.asString();
    this.bankAccountId = bankAccountId.asString();
  }

  public String getRecipientId() {
    return recipientId;
  }

  public String getBankAccountId() {
    return bankAccountId;
  }
}
