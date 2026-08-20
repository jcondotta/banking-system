package com.jcondotta.banking.transfers.domain.monetary_movement.value_objects;

import com.jcondotta.banking.transfers.domain.monetary_movement.exceptions.NegativeMonetaryAmountException;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.jcondotta.domain.support.Preconditions.required;

public record MonetaryAmount(BigDecimal amount, Currency currency) {

    public static final String AMOUNT_NOT_PROVIDED = "amount must be provided.";
    public static final String CURRENCY_NOT_PROVIDED = "currency must be provided.";

    public MonetaryAmount {
        required(amount, AMOUNT_NOT_PROVIDED);
        required(currency, CURRENCY_NOT_PROVIDED);

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeMonetaryAmountException();
        }
    }

    public static MonetaryAmount of(BigDecimal amount, Currency currency) {
        return new MonetaryAmount(amount, currency);
    }
}