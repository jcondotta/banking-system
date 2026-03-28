package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountBlockedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import com.jcondotta.banking.accounts.infrastructure.fixtures.IntegrationEventMetadataFixture;
import com.jcondotta.banking.contracts.block.BankAccountBlockedIntegrationEvent;
import com.jcondotta.banking.contracts.block.BankAccountBlockedIntegrationPayload;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountBlockedIntegrationEventMapperTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();

  private final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  private final BankAccountBlockedIntegrationEventMapper mapper =
    new BankAccountBlockedIntegrationEventMapper();

  @Test
  void shouldMapToBankAccountBlockedIntegrationEvent_whenDomainEventIsValid() {
    var metadata = IntegrationEventMetadataFixture.create();

    var event = new BankAccountBlockedEvent(
      EVENT_ID,
      BANK_ACCOUNT_ID,
      NOW
    );

    var integrationEvent = mapper.map(metadata, event);

    assertThat(integrationEvent)
      .isExactlyInstanceOf(BankAccountBlockedIntegrationEvent.class);

    assertThat(integrationEvent.metadata()).isEqualTo(metadata);
    assertThat(integrationEvent.eventType())
      .isEqualTo(BankAccountBlockedIntegrationEvent.EVENT_TYPE);

    var payload = integrationEvent.payload();

    assertThat(payload)
      .extracting(BankAccountBlockedIntegrationPayload::bankAccountId)
      .isEqualTo(BANK_ACCOUNT_ID.value());
  }

  @Test
  void shouldReturnBankAccountBlockedEventClass_whenDomainEventTypeIsRequested() {
    assertThat(mapper.domainEventType()).isEqualTo(BankAccountBlockedEvent.class);
  }
}