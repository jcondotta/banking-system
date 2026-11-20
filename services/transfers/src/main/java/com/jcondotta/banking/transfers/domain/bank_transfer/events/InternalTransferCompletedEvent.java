package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record InternalTransferCompletedEvent(
    DomainEventMetadata<BankTransferId> metadata,
    InternalTransferCompletedData data
) implements DomainEvent<BankTransferId, InternalTransferCompletedData> {

    public static final String EVENT_TYPE = "internal-transfer-completed";

    public InternalTransferCompletedEvent {
        required(metadata, DomainEventErrors.EVENT_METADATA_MUST_BE_PROVIDED);
        required(data, DomainEventErrors.EVENT_DATA_MUST_BE_PROVIDED);
    }

    public InternalTransferCompletedEvent(EventId eventId, BankTransferId aggregateId, Instant occurredAt) {
        this(
            DomainEventMetadata.of(eventId, aggregateId, occurredAt),
            new InternalTransferCompletedData()
        );
    }

    @Override
    public String eventType() {
        return EVENT_TYPE;
    }
}
