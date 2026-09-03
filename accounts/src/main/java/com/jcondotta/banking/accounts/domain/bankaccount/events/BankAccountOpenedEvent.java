package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountOpenedEvent(
  DomainEventMetadata<BankAccountId> metadata,
  BankAccountOpenedData data
) implements DomainEvent<BankAccountId, BankAccountOpenedData> {

  public static final String EVENT_TYPE = "bank-account-opened";

  public BankAccountOpenedEvent {
    required(metadata, DomainEventErrors.EVENT_METADATA_MUST_BE_PROVIDED);
    required(data, DomainEventErrors.EVENT_DATA_MUST_BE_PROVIDED);
  }

  public BankAccountOpenedEvent(
    EventId eventId,
    BankAccountId aggregateId,
    AccountType accountType,
    Currency currency,
    AccountHolderId primaryAccountHolderId,
    Instant occurredAt
  ) {
    this(
      DomainEventMetadata.of(eventId, aggregateId, occurredAt),
      new BankAccountOpenedData(accountType, currency, primaryAccountHolderId)
    );
  }

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }

  public AccountType accountType() {
    return data.accountType();
  }

  public Currency currency() {
    return data.currency();
  }

  public AccountHolderId primaryAccountHolderId() {
    return data.primaryAccountHolderId();
  }
}
