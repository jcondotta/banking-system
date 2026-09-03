package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedData;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountStatusChangedPublicationFactoryTest {

  private static final EventId EVENT_ID = EventId.of(UUID.fromString("9f1c2a44-6b7e-4c1a-8d3e-2f7a9b6c5d01"));
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.fromString("6a3a7a45-21ee-4110-9d9a-b619fccd88a6"));
  private static final Instant OCCURRED_AT = Instant.parse("2026-04-08T10:00:00Z");

  private final BankAccountStatusChangedPublicationFactory factory = new BankAccountStatusChangedPublicationFactory();

  @Test
  void shouldCreatePublication_whenBankAccountStatusChangedEvent() {
    var event = bankAccountStatusChangedEvent();

    var publication = factory.create(event);

    assertThat(factory.domainEventType()).isEqualTo(BankAccountStatusChangedEvent.class);
    assertThat(publication.event()).isSameAs(event);
    assertThat(publication.destination()).isEqualTo("bank-account-status-changed");
    assertThat(publication.key()).isEqualTo(BANK_ACCOUNT_ID.asString());
  }

  private static BankAccountStatusChangedEvent bankAccountStatusChangedEvent() {
    return new BankAccountStatusChangedEvent(
      DomainEventMetadata.of(EVENT_ID, BANK_ACCOUNT_ID, OCCURRED_AT),
      new BankAccountStatusChangedData(AccountStatus.PENDING, AccountStatus.ACTIVE)
    );
  }
}
