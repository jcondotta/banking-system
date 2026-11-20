package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountActivatedEvent(
  DomainEventMetadata<BankAccountId> metadata,
  BankAccountActivatedData data
) implements DomainEvent<BankAccountId, BankAccountActivatedData> {

  public static final String EVENT_TYPE = "bank-account-activated";

  public BankAccountActivatedEvent {
    required(metadata, DomainEventErrors.EVENT_METADATA_MUST_BE_PROVIDED);
    required(data, DomainEventErrors.EVENT_DATA_MUST_BE_PROVIDED);
  }

  public BankAccountActivatedEvent(
    EventId eventId,
    BankAccountId aggregateId,
    Iban iban,
    Currency currency,
    Instant occurredAt
  ) {
    this(
      DomainEventMetadata.of(eventId, aggregateId, occurredAt),
      new BankAccountActivatedData(required(iban, BankAccountErrors.IBAN_MUST_BE_PROVIDED).value(), currency)
    );
  }

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }

  public String iban() {
    return data.iban();
  }

  public Currency currency() {
    return data.currency();
  }
}
