package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.domain.exception.DomainConflictException;

public abstract class RecipientDomainConflictException extends DomainConflictException {

  protected RecipientDomainConflictException(String message) {
    super(message);
  }
}
