package com.jcondotta.banking.transfers.domain.bank_account.enums;

public enum BankAccountStatus {
    PENDING,
    ACTIVE,
    BLOCKED,
    CLOSED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
