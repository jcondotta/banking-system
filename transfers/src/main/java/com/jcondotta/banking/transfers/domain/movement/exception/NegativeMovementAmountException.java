package com.jcondotta.banking.transfers.domain.movement.exception;

import com.jcondotta.domain.exception.DomainRuleViolationException;

public class NegativeMovementAmountException extends DomainRuleViolationException {

    public static final String AMOUNT_NOT_NEGATIVE_MESSAGE = "Movement amount must not be negative.";

    public NegativeMovementAmountException() {
        super(AMOUNT_NOT_NEGATIVE_MESSAGE);
    }
}
