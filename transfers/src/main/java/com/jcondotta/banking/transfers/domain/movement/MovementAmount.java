package com.jcondotta.banking.transfers.domain.movement;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.money.Money;
import com.jcondotta.banking.transfers.domain.movement.exception.NegativeMovementAmountException;

import java.math.BigDecimal;

import static com.jcondotta.domain.support.Preconditions.required;

public record MovementAmount(Money money) {

    public static final String MONEY_NOT_PROVIDED = "money must be provided";

    public MovementAmount {
        required(money, MONEY_NOT_PROVIDED);

        if (money.isNegative()) {
            throw new NegativeMovementAmountException();
        }
    }

    public static MovementAmount of(BigDecimal amount, Currency currency) {
        return new MovementAmount(Money.of(amount, currency));
    }

    public BigDecimal amount() {
        return money.amount();
    }

    public Currency currency() {
        return money.currency();
    }
}
