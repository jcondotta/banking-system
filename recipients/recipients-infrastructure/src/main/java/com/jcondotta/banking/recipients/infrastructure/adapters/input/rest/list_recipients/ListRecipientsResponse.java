package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQueryResult;

import java.util.List;

public record ListRecipientsResponse(List<RecipientRestResponse> recipients) {

  public ListRecipientsResponse(List<RecipientRestResponse> recipients) {
    this.recipients = List.copyOf(recipients);
  }

  static ListRecipientsResponse from(ListRecipientsQueryResult queryResult) {
    return new ListRecipientsResponse(
      queryResult.recipients().stream()
        .map(RecipientRestResponse::from)
        .toList()
    );
  }
}
