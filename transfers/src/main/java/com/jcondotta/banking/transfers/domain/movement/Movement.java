package com.jcondotta.banking.transfers.domain.movement;

import com.jcondotta.banking.money.Currency;

import java.math.BigDecimal;

import static com.jcondotta.domain.support.Preconditions.required;

public record Movement(MovementType movementType, MovementAmount movementAmount) {

    public static final String MOVEMENT_AMOUNT_NOT_PROVIDED = "movement amount must be provided";
    public static final String MOVEMENT_TYPE_NOT_PROVIDED = "movement type must be provided";

    public Movement {
        required(movementType, MOVEMENT_TYPE_NOT_PROVIDED);
        required(movementAmount, MOVEMENT_AMOUNT_NOT_PROVIDED);
    }

    public static Movement of(MovementType movementType, MovementAmount movementAmount) {
        return new Movement(movementType, movementAmount);
    }

    public static Movement ofDebit(MovementAmount movementAmount) {
        return new Movement(MovementType.DEBIT, movementAmount);
    }

    public static Movement ofCredit(MovementAmount movementAmount) {
        return new Movement(MovementType.CREDIT, movementAmount);
    }

    public BigDecimal amount() {
        return movementAmount.amount();
    }

    public Currency currency() {
        return movementAmount.currency();
    }

    public boolean isDebit() {
        return movementType.isDebit();
    }

    public boolean isCredit() {
        return movementType.isCredit();
    }
}
