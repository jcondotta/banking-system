package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountJointHolderAddedEvent(
  DomainEventMetadata<BankAccountId> metadata,
  BankAccountJointHolderAddedData data
) implements DomainEvent<BankAccountId, BankAccountJointHolderAddedData> {

  public static final String EVENT_TYPE = "bank-account-joint-holder-added";

  public BankAccountJointHolderAddedEvent {
    required(metadata, DomainEventErrors.EVENT_METADATA_MUST_BE_PROVIDED);
    required(data, DomainEventErrors.EVENT_DATA_MUST_BE_PROVIDED);
  }

  public BankAccountJointHolderAddedEvent(
    EventId eventId,
    BankAccountId aggregateId,
    AccountHolderId accountHolderId,
    Instant occurredAt
  ) {
    this(
      DomainEventMetadata.of(eventId, aggregateId, occurredAt),
      new BankAccountJointHolderAddedData(accountHolderId)
    );
  }

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }

  public AccountHolderId accountHolderId() {
    return data.accountHolderId();
  }
}
