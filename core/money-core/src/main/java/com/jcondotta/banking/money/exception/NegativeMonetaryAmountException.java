package com.jcondotta.banking.money.exception;

import com.jcondotta.domain.exception.DomainRuleViolationException;

public class NegativeMonetaryAmountException extends DomainRuleViolationException {

    public static final String AMOUNT_NOT_NEGATIVE_MESSAGE = "Monetary amount must not be negative.";

    public NegativeMonetaryAmountException() {
        super(AMOUNT_NOT_NEGATIVE_MESSAGE);
    }
}
