package com.jcondotta.banking.recipients.domain.testsupport;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;

public enum RecipientFixtures {

  JEFFERSON(RecipientTestData.JEFFERSON),
  PATRIZIO(RecipientTestData.PATRIZIO),
  VIRGINIO(RecipientTestData.VIRGINIO);

  private final RecipientTestData testData;

  RecipientFixtures(RecipientTestData testData) {
    this.testData = testData;
  }

  public RecipientName toName() {
    return RecipientName.of(testData.getName());
  }

  public Iban toIban() {
    return Iban.of(testData.getIban());
  }

  public Recipient toRecipient(BankAccountId bankAccountId) {
    return Recipient.create(
      RecipientId.newId(),
      bankAccountId,
      toName(),
      toIban(),
      TimeFactory.FIXED_INSTANT
    );
  }
}
