package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.DomainPreconditions.required;

public record BankAccountOpenedEvent(
  EventId eventId,
  BankAccountId aggregateId,
  AccountType accountType,
  Currency currency,
  AccountHolderId primaryAccountHolderId,
  Instant occurredAt
) implements DomainEvent<BankAccountId> {

  public BankAccountOpenedEvent {
    required(eventId, DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
    required(aggregateId, DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
    required(accountType, BankAccountErrors.ACCOUNT_TYPE_MUST_BE_PROVIDED);
    required(currency, BankAccountErrors.CURRENCY_MUST_BE_PROVIDED);
    required(primaryAccountHolderId, AccountHolderId.ACCOUNT_HOLDER_ID_NOT_PROVIDED);
    required(occurredAt, DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
  }
}