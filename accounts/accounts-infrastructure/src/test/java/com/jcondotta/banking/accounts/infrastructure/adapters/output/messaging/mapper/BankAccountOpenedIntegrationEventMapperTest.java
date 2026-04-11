package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import com.jcondotta.banking.accounts.infrastructure.fixtures.IntegrationEventMetadataFixture;
import com.jcondotta.banking.accounts.contracts.open.BankAccountOpenedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.open.BankAccountOpenedIntegrationPayload;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountOpenedIntegrationEventMapperTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
  private static final AccountHolderId ACCOUNT_HOLDER_ID = AccountHolderId.newId();

  private final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  private final BankAccountOpenedIntegrationEventMapper mapper = new BankAccountOpenedIntegrationEventMapper();

  @Test
  void shouldMapToBankAccountOpenedIntegrationEvent_whenDomainEventIsValid() {
    var metadata = IntegrationEventMetadataFixture.create();

    var event = new BankAccountOpenedEvent(
      EVENT_ID,
      BANK_ACCOUNT_ID,
      AccountType.CHECKING,
      Currency.EUR,
      ACCOUNT_HOLDER_ID,
      NOW
    );

    var integrationEvent = mapper.map(metadata, event);

    assertThat(integrationEvent)
      .isExactlyInstanceOf(BankAccountOpenedIntegrationEvent.class);

    assertThat(integrationEvent.metadata()).isEqualTo(metadata);
    assertThat(integrationEvent.eventType())
      .isEqualTo(BankAccountOpenedIntegrationEvent.EVENT_TYPE);

    var payload = integrationEvent.payload();

    assertThat(payload)
      .extracting(
        BankAccountOpenedIntegrationPayload::bankAccountId,
        BankAccountOpenedIntegrationPayload::accountType,
        BankAccountOpenedIntegrationPayload::currency,
        BankAccountOpenedIntegrationPayload::accountHolderId
      )
      .containsExactly(
        BANK_ACCOUNT_ID.value(),
        AccountType.CHECKING.name(),
        Currency.EUR.name(),
        ACCOUNT_HOLDER_ID.value()
      );
  }

  @Test
  void shouldReturnBankAccountOpenedEventClass_whenDomainEventTypeIsRequested() {
    assertThat(mapper.domainEventType()).isEqualTo(BankAccountOpenedEvent.class);
  }
}