package com.jcondotta.banking.transfers.domain.bank_transfer.identity;

import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.support.Preconditions;

import java.util.UUID;

public record BankTransferId(UUID value) implements AggregateId<UUID> {

    public static final String ID_NOT_PROVIDED = "bank transfer id value must be provided";

    public BankTransferId {
        Preconditions.required(value, ID_NOT_PROVIDED);
    }

    public static BankTransferId of(UUID value) {
        return new BankTransferId(value);
    }

    public static BankTransferId newId() {
        return new BankTransferId(UUID.randomUUID());
    }
}

