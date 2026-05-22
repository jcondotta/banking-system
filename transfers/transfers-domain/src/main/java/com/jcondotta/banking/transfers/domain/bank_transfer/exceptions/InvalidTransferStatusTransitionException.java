package com.jcondotta.banking.transfers.domain.bank_transfer.exceptions;

import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferStatus;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public final class InvalidTransferStatusTransitionException extends DomainRuleViolationException {

  public InvalidTransferStatusTransitionException(TransferStatus from, TransferStatus to) {
    super("Cannot transition bank transfer from " + from + " to " + to);
  }
}
