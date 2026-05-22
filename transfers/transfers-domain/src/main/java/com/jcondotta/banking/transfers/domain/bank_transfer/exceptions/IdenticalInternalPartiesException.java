package com.jcondotta.banking.transfers.domain.bank_transfer.exceptions;

import com.jcondotta.domain.exception.DomainRuleViolationException;

public class IdenticalInternalPartiesException extends DomainRuleViolationException {

    public static final String MESSAGE = "Sender and recipient bank accounts must not be identical.";

    public IdenticalInternalPartiesException() {
        super(MESSAGE);
    }
}