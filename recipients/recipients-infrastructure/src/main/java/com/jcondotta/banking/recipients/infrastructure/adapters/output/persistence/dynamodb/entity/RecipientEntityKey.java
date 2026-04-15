package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;

public final class RecipientEntityKey {

  public static final String RECIPIENT_PK_TEMPLATE = "BANK_ACCOUNT#%s";
  public static final String RECIPIENT_SK_TEMPLATE = "RECIPIENT#%s";

  private RecipientEntityKey() {
  }

  public static String partitionKey(BankAccountId bankAccountId) {
    return RECIPIENT_PK_TEMPLATE.formatted(bankAccountId.value().toString());
  }

  public static String sortKey(RecipientId recipientId) {
    return RECIPIENT_SK_TEMPLATE.formatted(recipientId.value().toString());
  }
}