package com.jcondotta.banking.ledger.domain.ledger_account.entries;

import com.jcondotta.banking.ledger.domain.ledger_account.identity.LedgerAccountId;
import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.movement.MovementAmount;
import com.jcondotta.banking.movement.MovementType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.jcondotta.domain.support.Preconditions.required;

public record LedgerEntry(
        UUID id,
        LedgerAccountId accountId,
        UUID transferId,
        MovementType movementType,
        BigDecimal amount,
        Currency currency,
        Instant createdAt
) {

    private static final String ID_REQUIRED = "Ledger entry id must be provided";
    private static final String ACCOUNT_ID_REQUIRED = "Ledger entry account id must be provided";
    private static final String TRANSFER_ID_REQUIRED = "Ledger entry transfer id must be provided";
    private static final String MOVEMENT_TYPE_REQUIRED = "Ledger entry movement type must be provided";
    private static final String AMOUNT_REQUIRED = "Ledger entry amount must be provided";
    private static final String CURRENCY_REQUIRED = "Ledger entry currency must be provided";
    private static final String CREATED_AT_REQUIRED = "Ledger entry created at must be provided";

    public LedgerEntry {
        required(id, ID_REQUIRED);
        required(accountId, ACCOUNT_ID_REQUIRED);
        required(transferId, TRANSFER_ID_REQUIRED);
        required(movementType, MOVEMENT_TYPE_REQUIRED);
        required(amount, AMOUNT_REQUIRED);
        required(currency, CURRENCY_REQUIRED);
        required(createdAt, CREATED_AT_REQUIRED);
    }

    private static final String MOVEMENT_AMOUNT_REQUIRED = "Ledger entry movement amount must be provided";

    public static LedgerEntry of(
            LedgerAccountId accountId,
            UUID transferId,
            MovementType movementType,
            MovementAmount movementAmount,
            Instant createdAt
    ) {
        required(movementAmount, MOVEMENT_AMOUNT_REQUIRED);
        return new LedgerEntry(
                UUID.randomUUID(),
                accountId,
                transferId,
                movementType,
                movementAmount.amount(),
                movementAmount.currency(),
                createdAt
        );
    }
}
