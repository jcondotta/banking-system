package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsQueryResult;

import java.util.List;

public record ListRecipientsResponse(
  List<RecipientRestResponse> recipients,
  int page,
  int size,
  long totalElements,
  int totalPages,
  boolean hasNext,
  boolean hasPrevious
) {

  public ListRecipientsResponse {
    recipients = List.copyOf(recipients);
  }

  static ListRecipientsResponse from(ListRecipientsQueryResult queryResult) {
    var page = queryResult.page();

    return new ListRecipientsResponse(
      page.content().stream()
        .map(RecipientRestResponse::from)
        .toList(),
      page.page(),
      page.size(),
      page.totalElements(),
      page.totalPages(),
      page.hasNext(),
      page.hasPrevious()
    );
  }
}
