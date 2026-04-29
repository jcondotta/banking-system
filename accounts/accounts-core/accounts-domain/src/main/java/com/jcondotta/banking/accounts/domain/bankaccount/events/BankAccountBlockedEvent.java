package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountBlockedEvent(
  EventId eventId,
  BankAccountId aggregateId,
  Instant occurredAt
) implements DomainEvent<BankAccountId> {

  public BankAccountBlockedEvent {
    required(eventId, DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
    required(aggregateId, DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
    required(occurredAt, DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
  }
}
