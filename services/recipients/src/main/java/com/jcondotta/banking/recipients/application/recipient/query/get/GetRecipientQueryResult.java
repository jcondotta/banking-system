package com.jcondotta.banking.recipients.application.recipient.query.get;

import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;

import static java.util.Objects.requireNonNull;

public record GetRecipientQueryResult(RecipientSummary recipient) {

  static final String RECIPIENT_REQUIRED = "recipient must be provided";

  public GetRecipientQueryResult {
    requireNonNull(recipient, RECIPIENT_REQUIRED);
  }
}
