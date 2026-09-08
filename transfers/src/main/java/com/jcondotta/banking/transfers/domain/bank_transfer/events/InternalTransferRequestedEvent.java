package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.validation.BankTransferErrors;
import com.jcondotta.banking.transfers.domain.movement.MovementAmount;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.math.BigDecimal;
import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record InternalTransferRequestedEvent(
    DomainEventMetadata<BankTransferId> metadata,
    InternalTransferRequestedData data
) implements DomainEvent<BankTransferId, InternalTransferRequestedData> {

    public static final String EVENT_TYPE = "internal-transfer-requested";

    public InternalTransferRequestedEvent {
        required(metadata, DomainEventErrors.EVENT_METADATA_MUST_BE_PROVIDED);
        required(data, DomainEventErrors.EVENT_DATA_MUST_BE_PROVIDED);
    }

    public InternalTransferRequestedEvent(
        EventId eventId,
        BankTransferId aggregateId,
        BankAccountId senderAccountId,
        BankAccountId recipientAccountId,
        MovementAmount movementAmount,
        String reference,
        Instant occurredAt
    ) {
        this(
            DomainEventMetadata.of(eventId, aggregateId, occurredAt),
            createData(senderAccountId, recipientAccountId, movementAmount, reference)
        );
    }

    private static InternalTransferRequestedData createData(
        BankAccountId senderAccountId,
        BankAccountId recipientAccountId,
        MovementAmount movementAmount,
        String reference
    ) {
        required(movementAmount, BankTransferErrors.MOVEMENT_AMOUNT_MUST_BE_PROVIDED);

        return new InternalTransferRequestedData(
            senderAccountId,
            recipientAccountId,
            movementAmount.amount(),
            movementAmount.currency(),
            reference
        );
    }

    @Override
    public String eventType() {
        return EVENT_TYPE;
    }

    public BankAccountId senderAccountId() {
        return data.senderAccountId();
    }

    public BankAccountId recipientAccountId() {
        return data.recipientAccountId();
    }

    public BigDecimal amount() {
        return data.amount();
    }

    public Currency currency() {
        return data.currency();
    }

    public String reference() {
        return data.reference();
    }
}
