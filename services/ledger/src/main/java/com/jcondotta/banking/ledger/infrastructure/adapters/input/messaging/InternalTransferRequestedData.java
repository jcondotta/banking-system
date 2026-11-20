package com.jcondotta.banking.ledger.infrastructure.adapters.input.messaging;

import java.math.BigDecimal;
import java.util.UUID;

public record InternalTransferRequestedData(
        UUID senderAccountId,
        UUID recipientAccountId,
        BigDecimal amount,
        String currency,
        String reference
) {}
