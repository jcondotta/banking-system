package com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects;

import com.jcondotta.domain.support.Preconditions;

import java.util.UUID;

public record AccountReference(UUID value) {

    public static final String ACCOUNT_REFERENCE_NOT_PROVIDED = "Account reference must be provided";

    public AccountReference {
        Preconditions.required(value, ACCOUNT_REFERENCE_NOT_PROVIDED);
    }

    public static AccountReference of(UUID value) {
        return new AccountReference(value);
    }
}
