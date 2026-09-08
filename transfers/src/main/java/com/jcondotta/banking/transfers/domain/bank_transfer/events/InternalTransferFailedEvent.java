package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record InternalTransferFailedEvent(
    DomainEventMetadata<BankTransferId> metadata,
    InternalTransferFailedData data
) implements DomainEvent<BankTransferId, InternalTransferFailedData> {

    public static final String EVENT_TYPE = "internal-transfer-failed";

    public InternalTransferFailedEvent {
        required(metadata, DomainEventErrors.EVENT_METADATA_MUST_BE_PROVIDED);
        required(data, DomainEventErrors.EVENT_DATA_MUST_BE_PROVIDED);
    }

    public InternalTransferFailedEvent(EventId eventId, BankTransferId aggregateId, Instant occurredAt) {
        this(
            DomainEventMetadata.of(eventId, aggregateId, occurredAt),
            new InternalTransferFailedData()
        );
    }

    @Override
    public String eventType() {
        return EVENT_TYPE;
    }
}
