package com.jcondotta.banking.movement;

public enum MovementType {
    DEBIT, CREDIT;

    public boolean isDebit() {
        return this == DEBIT;
    }

    public boolean isCredit() {
        return this == CREDIT;
    }
}
