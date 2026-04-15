package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.domain.exception.DomainConflictException;

public final class DuplicateRecipientException extends DomainConflictException {

  public static final String RECIPIENT_WITH_IBAN_ALREADY_EXISTS = "Recipient with IBAN '%s' already exists";

  public DuplicateRecipientException(String iban) {
    super(RECIPIENT_WITH_IBAN_ALREADY_EXISTS.formatted(iban));
  }
}