package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountJointHolderDeactivatedEvent(
  EventId eventId,
  BankAccountId aggregateId,
  AccountHolderId accountHolderId,
  Instant occurredAt
) implements DomainEvent<BankAccountId> {

  public BankAccountJointHolderDeactivatedEvent {
    required(eventId, DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
    required(aggregateId, DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
    required(accountHolderId, AccountHolderId.ACCOUNT_HOLDER_ID_NOT_PROVIDED);
    required(occurredAt, DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
  }
}
