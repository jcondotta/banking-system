package com.jcondotta.banking.transfers.ledger.infrastructure.adapters.input.messaging;

import java.time.Instant;
import java.util.UUID;

public record BankAccountActivatedMessage(
        String eventId,
        UUID correlationId,
        String aggregateId,
        String eventType,
        String eventSource,
        Instant occurredAt,
        int eventVersion,
        BankAccountActivatedMessageData data
) {

    public record BankAccountActivatedMessageData(String iban, String currency) {}
}
