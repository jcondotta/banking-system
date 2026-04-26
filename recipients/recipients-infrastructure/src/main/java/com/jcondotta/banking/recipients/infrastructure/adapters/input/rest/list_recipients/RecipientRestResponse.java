package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;

import java.time.Instant;
import java.util.UUID;

public record RecipientRestResponse(
  UUID recipientId,
  UUID bankAccountId,
  String recipientName,
  String iban,
  Instant createdAt
) {

  static RecipientRestResponse from(RecipientSummary summary) {
    return new RecipientRestResponse(
      summary.recipientId(),
      summary.bankAccountId(),
      summary.recipientName(),
      summary.iban(),
      summary.createdAt()
    );
  }
}
