package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountStatusChangedEvent(
  DomainEventMetadata<BankAccountId> metadata,
  BankAccountStatusChangedData data
) implements DomainEvent<BankAccountId, BankAccountStatusChangedData> {

  public static final String EVENT_TYPE = "bank-account-status-changed";

  public BankAccountStatusChangedEvent {
    required(metadata, DomainEventErrors.EVENT_METADATA_MUST_BE_PROVIDED);
    required(data, DomainEventErrors.EVENT_DATA_MUST_BE_PROVIDED);
  }

  public BankAccountStatusChangedEvent(
    EventId eventId,
    BankAccountId aggregateId,
    AccountStatus previousStatus,
    AccountStatus currentStatus,
    Instant occurredAt
  ) {
    this(
      DomainEventMetadata.of(eventId, aggregateId, occurredAt),
      new BankAccountStatusChangedData(previousStatus, currentStatus)
    );
  }

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }

  public AccountStatus previousStatus() {
    return data.previousStatus();
  }

  public AccountStatus currentStatus() {
    return data.currentStatus();
  }
}
