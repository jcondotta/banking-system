package com.jcondotta.banking.transfers.ledger.infrastructure.adapters.input.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record InternalTransferRequestedMessage(
        String eventId,
        UUID correlationId,
        String aggregateId,
        String eventType,
        String eventSource,
        Instant occurredAt,
        int eventVersion,
        InternalTransferRequestedMessageData data
) {

    public record InternalTransferRequestedMessageData(
            UUID senderAccountId,
            UUID recipientAccountId,
            MonetaryAmountData amount,
            String reference
    ) {

        public record MonetaryAmountData(BigDecimal amount, String currency) {}
    }
}
