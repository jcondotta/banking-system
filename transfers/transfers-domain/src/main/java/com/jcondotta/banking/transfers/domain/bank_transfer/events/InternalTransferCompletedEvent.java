package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record InternalTransferCompletedEvent(
    EventId eventId,
    BankTransferId aggregateId,
    Instant occurredAt
) implements DomainEvent<BankTransferId> {

  public InternalTransferCompletedEvent {
    required(eventId, DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
    required(aggregateId, DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
    required(occurredAt, DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
  }
}
