package com.jcondotta.banking.transfers.ledger.domain.ledger_account.exceptions;

import com.jcondotta.banking.transfers.domain.common.FailureReason;
import com.jcondotta.banking.transfers.domain.common.FailureReasonProvider;
import com.jcondotta.domain.exception.DomainNotFoundException;

import java.util.UUID;

public final class LedgerAccountNotFoundException extends DomainNotFoundException implements FailureReasonProvider {

    public static final String MESSAGE = "Ledger account not found";

    private final UUID accountReference;

    public LedgerAccountNotFoundException(UUID accountReference) {
        super(MESSAGE);
        this.accountReference = accountReference;
    }

    @Override
    public FailureReason reason() {
        return FailureReason.NOT_FOUND;
    }

    public UUID getAccountReference() {
        return accountReference;
    }
}
