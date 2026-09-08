package com.jcondotta.banking.transfers.ledger.domain.ledger_account.exceptions;

import com.jcondotta.domain.exception.DomainNotFoundException;

import java.util.UUID;

public final class LedgerAccountNotFoundException extends DomainNotFoundException {

    public static final String MESSAGE = "Ledger account not found";

    private final UUID accountReference;

    public LedgerAccountNotFoundException(UUID accountReference) {
        super(MESSAGE);
        this.accountReference = accountReference;
    }

    public UUID getAccountReference() {
        return accountReference;
    }
}
