package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;

import java.util.List;

import static java.util.Objects.requireNonNull;

public record ListRecipientsQueryResult(List<RecipientSummary> recipients) {

  static final String RECIPIENTS_REQUIRED = "recipients must be provided";

  public ListRecipientsQueryResult(List<RecipientSummary> recipients) {
    requireNonNull(recipients, RECIPIENTS_REQUIRED);
    this.recipients = List.copyOf(recipients);
  }
}
