package com.jcondotta.banking.transfers.domain.monetary_movement.enums;

public enum MovementType {
    DEBIT, CREDIT;

    public boolean isDebit() {
        return this == DEBIT;
    }

    public boolean isCredit() {
        return this == CREDIT;
    }
}