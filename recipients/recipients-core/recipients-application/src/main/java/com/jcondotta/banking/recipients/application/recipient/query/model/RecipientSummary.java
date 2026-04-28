package com.jcondotta.banking.recipients.application.recipient.query.model;

import java.time.Instant;
import java.util.UUID;

public record RecipientSummary(
  UUID recipientId,
  UUID bankAccountId,
  String recipientName,
  String iban,
  Instant createdAt
) {}