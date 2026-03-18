package com.jcondotta.banking.recipients.domain.recipient.fixtures;

import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;

import java.time.Instant;

public enum RecipientFixtures {

  JEFFERSON("Jefferson Condotta", "ES3801283316232166447417"),
  PATRIZIO("Patrizio Condotta", "IT93Q0300203280175171887193"),
  VIRGINIO("Virginio Condotta", "GB82WEST12345698765432");

  private final String name;
  private final String iban;

  RecipientFixtures(String name, String iban) {
    this.name = name;
    this.iban = iban;
  }

  public String getName() {
    return name;
  }

  public String getIban() {
    return iban;
  }

  public RecipientName toName() {
    return RecipientName.of(name);
  }

  public Iban toIban() {
    return Iban.of(iban);
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

  public Recipient createRemoved() {
    return Recipient.restore(
      RecipientId.newId(),
      toName(),
      toIban(),
      RecipientStatus.REMOVED,
      Instant.now(ClockTestFactory.FIXED_CLOCK)
    );
  }
}