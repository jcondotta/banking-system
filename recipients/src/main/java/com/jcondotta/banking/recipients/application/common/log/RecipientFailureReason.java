package com.jcondotta.banking.recipients.application.common.log;

import com.jcondotta.banking.recipients.application.common.exception.RecipientOptimisticLockException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.domain.exception.DomainException;

import java.util.Locale;

public enum RecipientFailureReason {
  DUPLICATE_IBAN,
  NOT_FOUND,
  OPTIMISTIC_LOCK_CONFLICT,
  DOMAIN_ERROR,
  INTERNAL_ERROR;

  public String normalize() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static RecipientFailureReason from(DomainException exception) {
    if (exception == null) {
      return DOMAIN_ERROR;
    }

    return switch (exception) {
      case DuplicateRecipientIbanException ignored -> DUPLICATE_IBAN;
      case RecipientNotFoundException ignored -> NOT_FOUND;
      case RecipientOptimisticLockException ignored -> OPTIMISTIC_LOCK_CONFLICT;
      default -> DOMAIN_ERROR;
    };
  }
}
