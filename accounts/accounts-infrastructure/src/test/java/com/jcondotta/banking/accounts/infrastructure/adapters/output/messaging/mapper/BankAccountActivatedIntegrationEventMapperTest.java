package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import com.jcondotta.banking.accounts.infrastructure.fixtures.IntegrationEventMetadataFixture;
import com.jcondotta.banking.contracts.activate.BankAccountActivatedIntegrationEvent;
import com.jcondotta.banking.contracts.activate.BankAccountActivatedIntegrationPayload;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountActivatedIntegrationEventMapperTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();

  private final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  private final BankAccountActivatedIntegrationEventMapper mapper =
    new BankAccountActivatedIntegrationEventMapper();

  @Test
  void shouldMapToBankAccountActivatedIntegrationEvent_whenDomainEventIsValid() {
    var metadata = IntegrationEventMetadataFixture.create();

    var event = new BankAccountActivatedEvent(
      EVENT_ID,
      BANK_ACCOUNT_ID,
      NOW
    );

    var integrationEvent = mapper.map(metadata, event);

    assertThat(integrationEvent)
      .isExactlyInstanceOf(BankAccountActivatedIntegrationEvent.class);

    assertThat(integrationEvent.metadata()).isEqualTo(metadata);
    assertThat(integrationEvent.eventType())
      .isEqualTo(BankAccountActivatedIntegrationEvent.EVENT_TYPE);

    var payload = integrationEvent.payload();

    assertThat(payload)
      .extracting(BankAccountActivatedIntegrationPayload::bankAccountId)
      .isEqualTo(BANK_ACCOUNT_ID.value());
  }

  @Test
  void shouldReturnBankAccountActivatedEventClass_whenDomainEventTypeIsRequested() {
    assertThat(mapper.domainEventType()).isEqualTo(BankAccountActivatedEvent.class);
  }
}