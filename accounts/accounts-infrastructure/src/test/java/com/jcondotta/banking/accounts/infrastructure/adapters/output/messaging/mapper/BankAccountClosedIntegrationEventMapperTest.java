package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountClosedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import com.jcondotta.banking.accounts.infrastructure.fixtures.IntegrationEventMetadataFixture;
import com.jcondotta.banking.accounts.contracts.close.BankAccountClosedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.close.BankAccountClosedIntegrationPayload;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountClosedIntegrationEventMapperTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();

  private final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  private final BankAccountClosedIntegrationEventMapper mapper =
    new BankAccountClosedIntegrationEventMapper();

  @Test
  void shouldMapToBankAccountClosedIntegrationEvent_whenDomainEventIsValid() {
    var metadata = IntegrationEventMetadataFixture.create();

    var event = new BankAccountClosedEvent(
      EVENT_ID,
      BANK_ACCOUNT_ID,
      NOW
    );

    var integrationEvent = mapper.map(metadata, event);

    assertThat(integrationEvent)
      .isExactlyInstanceOf(BankAccountClosedIntegrationEvent.class);

    assertThat(integrationEvent.metadata()).isEqualTo(metadata);
    assertThat(integrationEvent.eventType())
      .isEqualTo(BankAccountClosedIntegrationEvent.EVENT_TYPE);

    var payload = integrationEvent.payload();

    assertThat(payload)
      .extracting(BankAccountClosedIntegrationPayload::bankAccountId)
      .isEqualTo(BANK_ACCOUNT_ID.value());
  }

  @Test
  void shouldReturnBankAccountClosedEventClass_whenDomainEventTypeIsRequested() {
    assertThat(mapper.domainEventType()).isEqualTo(BankAccountClosedEvent.class);
  }
}