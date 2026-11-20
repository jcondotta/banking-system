package com.jcondotta.banking.ledger.domain.ledger_account.value_objects;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.money.Money;
import com.jcondotta.banking.movement.Movement;

import java.math.BigDecimal;

import static com.jcondotta.domain.support.Preconditions.required;

public record Balance(Money money) {

    public static final String MONEY_NOT_PROVIDED = "balance money must be provided.";
    public static final String MONETARY_MOVEMENT_NOT_PROVIDED = "monetary movement must be provided.";
    public static final String OTHER_BALANCE_NOT_PROVIDED = "balance to subtract must be provided.";

    public Balance {
        required(money, MONEY_NOT_PROVIDED);
    }

    public static Balance of(BigDecimal amount, Currency currency) {
        return new Balance(Money.of(amount, currency));
    }

    public static Balance zero(Currency currency) {
        return new Balance(Money.of(BigDecimal.ZERO, currency));
    }

    public BigDecimal amount() {
        return money.amount();
    }

    public Currency currency() {
        return money.currency();
    }

    public Balance apply(Movement movement) {
        required(movement, MONETARY_MOVEMENT_NOT_PROVIDED);
        Money movementMoney = movement.movementAmount().money();
        Money result = movement.isDebit()
            ? money.subtract(movementMoney)
            : money.add(movementMoney);
        return new Balance(result);
    }

    public Balance subtract(Balance other) {
        required(other, OTHER_BALANCE_NOT_PROVIDED);
        return new Balance(money.subtract(other.money));
    }

    public boolean isNegative() {
        return money.isNegative();
    }
}
