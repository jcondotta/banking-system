package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.common.FailureReasonProvider;
import com.jcondotta.domain.exception.DomainConflictException;

public abstract class RecipientDomainConflictException extends DomainConflictException implements FailureReasonProvider {

  protected RecipientDomainConflictException(String message) {
    super(message);
  }
}
