package com.jcondotta.banking.recipients.application.recipient.query.get;

import com.jcondotta.application.query.Query;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;

import static java.util.Objects.requireNonNull;

public record GetRecipientQuery(
  BankAccountId bankAccountId,
  RecipientId recipientId
) implements Query<GetRecipientQueryResult> {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";
  static final String RECIPIENT_ID_REQUIRED = "recipientId must be provided";

  public GetRecipientQuery {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
    requireNonNull(recipientId, RECIPIENT_ID_REQUIRED);
  }
}
