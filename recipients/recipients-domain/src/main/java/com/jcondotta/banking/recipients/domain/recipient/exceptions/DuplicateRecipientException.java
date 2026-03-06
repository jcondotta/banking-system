package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.domain.exception.DomainValidationException;

public final class DuplicateRecipientException extends DomainValidationException {

  public static final String RECIPIENT_WITH_IBAN_ALREADY_EXISTS = "Recipient with IBAN '%s' already exists";

  public DuplicateRecipientException(Iban iban) {
    super(RECIPIENT_WITH_IBAN_ALREADY_EXISTS.formatted(iban.value()));
  }
}