package com.jcondotta.banking.recipients.domain.bankaccount.testsupport;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;

import java.time.Instant;

public enum RecipientFixtures {

  JEFFERSON(RecipientTestData.JEFFERSON),
  PATRIZIO(RecipientTestData.PATRIZIO),
  VIRGINIO(RecipientTestData.VIRGINIO);

  private final RecipientTestData testData;

  RecipientFixtures(RecipientTestData testData) {
    this.testData = testData;
  }

  public String getName() {
    return testData.getName();
  }

  public String getIban() {
    return testData.getIban();
  }

  public RecipientName toName() {
    return RecipientName.of(testData.getName());
  }

  public Iban toIban() {
    return Iban.of(testData.getIban());
  }

  public Recipient toRecipient() {
    return create();
  }

  public Recipient create() {
    return Recipient.restore(
      RecipientId.newId(),
      toName(),
      toIban(),
      RecipientStatus.ACTIVE,
      Instant.now(ClockTestFactory.FIXED_CLOCK)
    );
  }
}
