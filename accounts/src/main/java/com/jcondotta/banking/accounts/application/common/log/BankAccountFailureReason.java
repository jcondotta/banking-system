package com.jcondotta.banking.accounts.application.common.log;

import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.AccountHolderNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.CannotDeactivatePrimaryHolderException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountStateTransitionException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.MaxJointHoldersExceededException;
import com.jcondotta.domain.exception.DomainException;

import java.util.Locale;

public enum BankAccountFailureReason {
  NOT_FOUND,
  NOT_ACTIVE,
  INVALID_STATE_TRANSITION,
  MAX_JOINT_HOLDERS_EXCEEDED,
  ACCOUNT_HOLDER_NOT_FOUND,
  CANNOT_DEACTIVATE_PRIMARY_HOLDER,
  DOMAIN_ERROR,
  INTERNAL_ERROR;

  public String normalize() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static BankAccountFailureReason from(DomainException exception) {
    if (exception == null) {
      return DOMAIN_ERROR;
    }

    return switch (exception) {
      case BankAccountNotFoundException ignored -> NOT_FOUND;
      case AccountHolderNotFoundException ignored -> ACCOUNT_HOLDER_NOT_FOUND;
      case BankAccountNotActiveException ignored -> NOT_ACTIVE;
      case InvalidBankAccountStateTransitionException ignored -> INVALID_STATE_TRANSITION;
      case MaxJointHoldersExceededException ignored -> MAX_JOINT_HOLDERS_EXCEEDED;
      case CannotDeactivatePrimaryHolderException ignored -> CANNOT_DEACTIVATE_PRIMARY_HOLDER;
      default -> DOMAIN_ERROR;
    };
  }
}
