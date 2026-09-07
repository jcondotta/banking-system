package com.jcondotta.banking.money.exception;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public class CurrencyMismatchException extends DomainRuleViolationException {

    private static final String MESSAGE_TEMPLATE = "Currency mismatch: expected %s but was %s.";

    private final Currency expected;
    private final Currency actual;

    public CurrencyMismatchException(Currency expected, Currency actual) {
        super(String.format(MESSAGE_TEMPLATE, expected, actual));
        this.expected = expected;
        this.actual = actual;
    }

    public Currency getExpected() {
        return expected;
    }

    public Currency getActual() {
        return actual;
    }
}
