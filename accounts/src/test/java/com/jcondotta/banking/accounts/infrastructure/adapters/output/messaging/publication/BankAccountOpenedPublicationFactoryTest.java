package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedData;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountOpenedPublicationFactoryTest {

  private static final EventId EVENT_ID = EventId.of(UUID.fromString("9f1c2a44-6b7e-4c1a-8d3e-2f7a9b6c5d01"));
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.fromString("6a3a7a45-21ee-4110-9d9a-b619fccd88a6"));
  private static final Instant OCCURRED_AT = Instant.parse("2026-04-08T10:00:00Z");

  private final BankAccountOpenedPublicationFactory factory = new BankAccountOpenedPublicationFactory();

  @Test
  void shouldCreatePublication_whenBankAccountOpenedEvent() {
    var event = bankAccountOpenedEvent();

    var publication = factory.create(event);

    assertThat(factory.domainEventType()).isEqualTo(BankAccountOpenedEvent.class);
    assertThat(publication.event()).isSameAs(event);
    assertThat(publication.destination()).isEqualTo("bank-account-opened");
    assertThat(publication.key()).isEqualTo(BANK_ACCOUNT_ID.asString());
  }

  private static BankAccountOpenedEvent bankAccountOpenedEvent() {
    return new BankAccountOpenedEvent(
      DomainEventMetadata.of(EVENT_ID, BANK_ACCOUNT_ID, OCCURRED_AT),
      new BankAccountOpenedData(AccountType.CHECKING, Currency.EUR, AccountHolderId.newId())
    );
  }
}
