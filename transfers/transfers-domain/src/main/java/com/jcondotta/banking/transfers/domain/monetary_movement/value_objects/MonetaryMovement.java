package com.jcondotta.banking.transfers.domain.monetary_movement.value_objects;

import com.jcondotta.banking.transfers.domain.monetary_movement.enums.MovementType;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;

import java.math.BigDecimal;

import static com.jcondotta.domain.support.Preconditions.required;

public record MonetaryMovement(MovementType movementType, MonetaryAmount monetaryAmount) {

    public static final String MONETARY_AMOUNT_NOT_PROVIDED = "monetary amount must be provided.";
    public static final String MOVEMENT_TYPE_NOT_PROVIDED = "movement type must be provided.";

    public MonetaryMovement {
        required(movementType, MOVEMENT_TYPE_NOT_PROVIDED);
        required(monetaryAmount, MONETARY_AMOUNT_NOT_PROVIDED);
    }

    public static MonetaryMovement of(MovementType movementType, MonetaryAmount monetaryAmount) {
        return new MonetaryMovement(movementType, monetaryAmount);
    }

    public static MonetaryMovement ofDebit(MonetaryAmount monetaryAmount) {
        return new MonetaryMovement(MovementType.DEBIT, monetaryAmount);
    }

    public static MonetaryMovement ofCredit(MonetaryAmount monetaryAmount) {
        return new MonetaryMovement(MovementType.CREDIT, monetaryAmount);
    }

    public BigDecimal amount() {
        return monetaryAmount.amount();
    }

    public Currency currency() {
        return monetaryAmount.currency();
    }

    public boolean isDebit() {
        return movementType.isDebit();
    }

    public boolean isCredit() {
        return movementType.isCredit();
    }
}