package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.application.query.Query;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import static java.util.Objects.requireNonNull;

public record ListRecipientsQuery(BankAccountId bankAccountId)
  implements Query<ListRecipientsQueryResult> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";

  public ListRecipientsQuery {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
  }
}