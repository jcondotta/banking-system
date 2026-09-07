package com.jcondotta.banking.transfers.ledger.domain.ledger_account.identity;

import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.support.Preconditions;

import java.util.UUID;

public record LedgerAccountId(UUID value) implements AggregateId<UUID> {

    public static final String ID_NOT_PROVIDED = "Ledger account id must be provided";

    public LedgerAccountId {
        Preconditions.required(value, ID_NOT_PROVIDED);
    }

    public static LedgerAccountId of(UUID value) {
        return new LedgerAccountId(value);
    }

    public static LedgerAccountId newId() {
        return new LedgerAccountId(UUID.randomUUID());
    }
}
