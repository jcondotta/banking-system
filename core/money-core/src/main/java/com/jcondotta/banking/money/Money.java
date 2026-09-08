package com.jcondotta.banking.money;

import com.jcondotta.banking.money.exception.InvalidMonetaryScaleException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.jcondotta.domain.support.Preconditions.required;

public record Money(BigDecimal amount, Currency currency) {

    public static final String AMOUNT_NOT_PROVIDED = "amount must be provided.";
    public static final String CURRENCY_NOT_PROVIDED = "currency must be provided.";
    public static final String OTHER_MONEY_NOT_PROVIDED = "money to operate with must be provided.";

    public Money {
        required(amount, AMOUNT_NOT_PROVIDED);
        required(currency, CURRENCY_NOT_PROVIDED);

        if (amount.scale() > currency.scale()) {
            throw new InvalidMonetaryScaleException(currency, amount.scale());
        }

        amount = amount.setScale(currency.scale(), RoundingMode.UNNECESSARY);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public Money add(Money other) {
        required(other, OTHER_MONEY_NOT_PROVIDED);
        currency.requireSameAs(other.currency);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        required(other, OTHER_MONEY_NOT_PROVIDED);
        currency.requireSameAs(other.currency);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
}
