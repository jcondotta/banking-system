package com.jcondotta.banking.money;

import com.jcondotta.banking.money.exception.InvalidMonetaryScaleException;
import com.jcondotta.banking.money.exception.NegativeMonetaryAmountException;

import java.math.BigDecimal;

import static com.jcondotta.domain.support.Preconditions.required;

public record MonetaryAmount(BigDecimal amount, Currency currency) {

    public static final String AMOUNT_NOT_PROVIDED = "amount must be provided.";
    public static final String CURRENCY_NOT_PROVIDED = "currency must be provided.";

    public MonetaryAmount {
        required(amount, AMOUNT_NOT_PROVIDED);
        required(currency, CURRENCY_NOT_PROVIDED);

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeMonetaryAmountException();
        }

        if (amount.scale() > currency.scale()) {
            throw new InvalidMonetaryScaleException(currency, amount.scale());
        }

        amount = amount.setScale(currency.scale());
    }

    public static MonetaryAmount of(BigDecimal amount, Currency currency) {
        return new MonetaryAmount(amount, currency);
    }
}
