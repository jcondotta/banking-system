package com.jcondotta.banking.money.exception;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public class InvalidMonetaryScaleException extends DomainRuleViolationException {

    private static final String MESSAGE_TEMPLATE =
        "Invalid monetary scale for %s: maximum is %d but was %d.";

    private final Currency currency;
    private final int allowedScale;
    private final int actualScale;

    public InvalidMonetaryScaleException(Currency currency, int actualScale) {
        super(String.format(MESSAGE_TEMPLATE, currency, currency.scale(), actualScale));
        this.currency = currency;
        this.allowedScale = currency.scale();
        this.actualScale = actualScale;
    }

    public Currency getCurrency() {
        return currency;
    }

    public int getAllowedScale() {
        return allowedScale;
    }

    public int getActualScale() {
        return actualScale;
    }
}
