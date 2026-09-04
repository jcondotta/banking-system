package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

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
        MonetaryAmount monetaryAmount,
        String reference,
        Instant occurredAt
    ) {
        this(
            DomainEventMetadata.of(eventId, aggregateId, occurredAt),
            new InternalTransferRequestedData(senderAccountId, recipientAccountId, monetaryAmount, reference)
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

    public MonetaryAmount monetaryAmount() {
        return data.monetaryAmount();
    }

    public String reference() {
        return data.reference();
    }
}
