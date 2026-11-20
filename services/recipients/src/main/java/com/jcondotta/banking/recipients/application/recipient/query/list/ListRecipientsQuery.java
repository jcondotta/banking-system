package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.application.query.PageRequest;
import com.jcondotta.application.query.Query;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import static java.util.Objects.requireNonNull;

public record ListRecipientsQuery(
  BankAccountId bankAccountId,
  PageRequest pageRequest,
  ListRecipientsFilter filter
)
  implements Query<ListRecipientsQueryResult> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";
  static final String PAGE_REQUEST_REQUIRED = "pageRequest must be provided";
  static final String FILTER_REQUIRED = "filter must be provided";

  public ListRecipientsQuery {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
    requireNonNull(pageRequest, PAGE_REQUEST_REQUIRED);
    requireNonNull(filter, FILTER_REQUIRED);
  }
}
