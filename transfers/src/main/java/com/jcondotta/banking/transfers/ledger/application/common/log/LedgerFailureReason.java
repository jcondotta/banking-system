package com.jcondotta.banking.transfers.ledger.application.common.log;

import com.jcondotta.banking.transfers.ledger.domain.ledger_account.exceptions.LedgerAccountNotFoundException;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.exceptions.LedgerAccountNotYetProvisionedException;
import com.jcondotta.domain.exception.DomainException;

import java.util.Locale;

public enum LedgerFailureReason {
    NOT_FOUND,
    DOMAIN_ERROR,
    INTERNAL_ERROR;

    public String normalize() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static LedgerFailureReason from(DomainException exception) {
        if (exception == null) {
            return DOMAIN_ERROR;
        }

        return switch (exception) {
            case LedgerAccountNotFoundException ignored -> NOT_FOUND;
            case LedgerAccountNotYetProvisionedException ignored -> NOT_FOUND;
            default -> DOMAIN_ERROR;
        };
    }
}
