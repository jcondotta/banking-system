package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountUnblockedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import com.jcondotta.banking.accounts.infrastructure.fixtures.IntegrationEventMetadataFixture;
import com.jcondotta.banking.accounts.contracts.unblock.BankAccountUnblockedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.unblock.BankAccountUnblockedIntegrationPayload;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountUnblockedIntegrationEventMapperTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();

  private final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  private final BankAccountUnblockedIntegrationEventMapper mapper =
    new BankAccountUnblockedIntegrationEventMapper();

  @Test
  void shouldMapToBankAccountUnblockedIntegrationEvent_whenDomainEventIsValid() {
    var metadata = IntegrationEventMetadataFixture.create();

    var event = new BankAccountUnblockedEvent(
      EVENT_ID,
      BANK_ACCOUNT_ID,
      NOW
    );

    var integrationEvent = mapper.map(metadata, event);

    assertThat(integrationEvent)
      .isExactlyInstanceOf(BankAccountUnblockedIntegrationEvent.class);

    assertThat(integrationEvent.metadata()).isEqualTo(metadata);
    assertThat(integrationEvent.eventType())
      .isEqualTo(BankAccountUnblockedIntegrationEvent.EVENT_TYPE);

    var payload = integrationEvent.payload();

    assertThat(payload)
      .extracting(BankAccountUnblockedIntegrationPayload::bankAccountId)
      .isEqualTo(BANK_ACCOUNT_ID.value());
  }

  @Test
  void shouldReturnBankAccountUnblockedEventClass_whenDomainEventTypeIsRequested() {
    assertThat(mapper.domainEventType()).isEqualTo(BankAccountUnblockedEvent.class);
  }
}