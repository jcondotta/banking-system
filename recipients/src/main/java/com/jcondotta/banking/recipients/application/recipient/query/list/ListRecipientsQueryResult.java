package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.application.query.PageResult;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;

import java.util.List;

import static java.util.Objects.requireNonNull;

public record ListRecipientsQueryResult(PageResult<RecipientSummary> page) {

  static final String PAGE_REQUIRED = "page must be provided";

  public ListRecipientsQueryResult {
    requireNonNull(page, PAGE_REQUIRED);
  }

  public List<RecipientSummary> recipients() {
    return page.content();
  }
}
