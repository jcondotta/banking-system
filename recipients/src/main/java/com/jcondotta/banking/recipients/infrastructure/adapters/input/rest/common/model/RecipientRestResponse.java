package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.model;

import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;

import java.time.Instant;
import java.util.UUID;

public record RecipientRestResponse(
  UUID recipientId,
  String recipientName,
  String maskedIban,
  Instant createdAt
) {

  public static RecipientRestResponse from(RecipientSummary summary) {
    return new RecipientRestResponse(
      summary.recipientId(),
      summary.recipientName(),
      summary.iban(),
      summary.createdAt()
    );
  }
}
