package com.jcondotta.banking.transfers.ledger.domain.ledger_account.exceptions;

import com.jcondotta.domain.exception.DomainNotFoundException;

import java.util.UUID;

public final class LedgerAccountNotYetProvisionedException extends DomainNotFoundException {

    public static final String MESSAGE = "Ledger account not yet provisioned";

    private final UUID accountReference;

    public LedgerAccountNotYetProvisionedException(UUID accountReference) {
        super(MESSAGE);
        this.accountReference = accountReference;
    }

    public UUID getAccountReference() {
        return accountReference;
    }
}
