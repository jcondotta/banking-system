package com.jcondotta.banking.transfers.infrastructure.adapters.input.rest.request_internal;

import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;

import java.math.BigDecimal;
import java.util.UUID;

record RequestInternalTransferRequest(
  UUID senderAccountId,
  String recipientName,
  String recipientIban,
  BigDecimal amount,
  Currency currency,
  String reference
) {
}
