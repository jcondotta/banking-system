package com.jcondotta.banking.recipients.application.common.log;

import com.jcondotta.banking.recipients.application.common.exception.RecipientOptimisticLockException;
import com.jcondotta.banking.recipients.domain.bank_account.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.bank_account.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.domain.exception.DomainException;

import java.util.Locale;

public enum RecipientFailureReason {
  DUPLICATE_IBAN,
  BANK_ACCOUNT_NOT_FOUND,
  BANK_ACCOUNT_NOT_ACTIVE,
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
      case BankAccountNotFoundException ignored -> BANK_ACCOUNT_NOT_FOUND;
      case BankAccountNotActiveException ignored -> BANK_ACCOUNT_NOT_ACTIVE;
      case RecipientNotFoundException ignored -> NOT_FOUND;
      case RecipientOptimisticLockException ignored -> OPTIMISTIC_LOCK_CONFLICT;
      default -> DOMAIN_ERROR;
    };
  }
}
