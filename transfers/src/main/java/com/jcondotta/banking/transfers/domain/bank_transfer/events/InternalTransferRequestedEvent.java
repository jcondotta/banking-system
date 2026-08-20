package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.validation.BankTransferErrors;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record InternalTransferRequestedEvent(
    EventId eventId,
    BankTransferId aggregateId,
    BankAccountId senderAccountId,
    BankAccountId recipientAccountId,
    MonetaryAmount monetaryAmount,
    String reference,
    Instant occurredAt
) implements DomainEvent<BankTransferId> {

  public InternalTransferRequestedEvent {
    required(eventId, DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
    required(aggregateId, DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
    required(senderAccountId, BankTransferErrors.SENDER_ACCOUNT_ID_MUST_BE_PROVIDED);
    required(recipientAccountId, BankTransferErrors.RECIPIENT_ACCOUNT_ID_MUST_BE_PROVIDED);
    required(monetaryAmount, BankTransferErrors.MONETARY_AMOUNT_MUST_BE_PROVIDED);
    required(occurredAt, DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
  }
}
