package com.jcondotta.banking.transfers.application.common.log;

import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotActiveException;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotFoundException;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.SenderBankAccountNotActiveException;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.SenderBankAccountNotFoundException;
import com.jcondotta.domain.exception.DomainException;

import java.util.Locale;

public enum BankTransferFailureReason {
  NOT_FOUND,
  DOMAIN_ERROR,
  INTERNAL_ERROR;

  public String normalize() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static BankTransferFailureReason from(DomainException exception) {
    if (exception == null) {
      return DOMAIN_ERROR;
    }

    return switch (exception) {
      case SenderBankAccountNotFoundException ignored -> NOT_FOUND;
      case SenderBankAccountNotActiveException ignored -> DOMAIN_ERROR;
      case RecipientBankAccountNotFoundException ignored -> NOT_FOUND;
      case RecipientBankAccountNotActiveException ignored -> DOMAIN_ERROR;
      default -> DOMAIN_ERROR;
    };
  }
}
