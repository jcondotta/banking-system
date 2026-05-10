package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountStatusChangedEvent(
  EventId eventId,
  BankAccountId aggregateId,
  AccountStatus previousStatus,
  AccountStatus currentStatus,
  Instant occurredAt
) implements DomainEvent<BankAccountId> {

  public BankAccountStatusChangedEvent {
    required(eventId, DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
    required(aggregateId, DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
    required(previousStatus, BankAccountErrors.PREVIOUS_STATUS_MUST_BE_PROVIDED);
    required(currentStatus, BankAccountErrors.CURRENT_STATUS_MUST_BE_PROVIDED);
    required(occurredAt, DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
  }
}
