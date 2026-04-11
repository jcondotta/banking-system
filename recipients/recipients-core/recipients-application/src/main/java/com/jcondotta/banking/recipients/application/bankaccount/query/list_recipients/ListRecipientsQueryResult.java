package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;

import java.util.List;

import static java.util.Objects.requireNonNull;

public record ListRecipientsQueryResult(List<RecipientSummary> recipients) {

  static final String RECIPIENTS_REQUIRED = "recipients must be provided";

  public ListRecipientsQueryResult {
    requireNonNull(recipients, RECIPIENTS_REQUIRED);
  }
}