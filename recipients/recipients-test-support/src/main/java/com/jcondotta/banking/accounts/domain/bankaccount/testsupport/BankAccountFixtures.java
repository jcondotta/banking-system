package com.jcondotta.banking.accounts.domain.bankaccount.testsupport;


import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipients;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public enum BankAccountFixtures {

  WITH_NO_RECIPIENTS,
  WITH_ONE_RECIPIENT(RecipientFixtures.JEFFERSON),
  WITH_TWO_RECIPIENTS(RecipientFixtures.JEFFERSON, RecipientFixtures.PATRIZIO);

  private final List<RecipientFixtures> recipients;

  BankAccountFixtures(RecipientFixtures... fixtures) {
    this.recipients = List.of(fixtures);
  }

  public BankAccount create() {
    return BankAccount.restore(
      BankAccountId.of(UUID.randomUUID()),
      AccountStatus.ACTIVE,
      Recipients.of(
        recipients.stream()
          .map(RecipientFixtures::toRecipient)
          .toList()
      )
    );
  }

  public static BankAccount create(RecipientFixtures... recipientFixtures) {
    return BankAccount.restore(
      BankAccountId.of(UUID.randomUUID()),
      AccountStatus.ACTIVE,
      Recipients.of(
        Arrays.stream(recipientFixtures)
          .map(RecipientFixtures::toRecipient)
          .toList()
      )
    );
  }
}