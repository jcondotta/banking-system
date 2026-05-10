package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.contracts.status.BankAccountStatus;
import com.jcondotta.banking.accounts.contracts.status.BankAccountStatusChangedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.status.BankAccountStatusChangedIntegrationPayload;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import com.jcondotta.banking.accounts.infrastructure.fixtures.IntegrationEventMetadataFixture;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountStatusChangedIntegrationEventMapperTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();

  private final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  private final BankAccountStatusChangedIntegrationEventMapper mapper =
    new BankAccountStatusChangedIntegrationEventMapper();

  @Test
  void shouldMapToBankAccountStatusChangedIntegrationEvent_whenDomainEventIsValid() {
    var metadata = IntegrationEventMetadataFixture.create();

    var event = new BankAccountStatusChangedEvent(
      EVENT_ID,
      BANK_ACCOUNT_ID,
      AccountStatus.PENDING,
      AccountStatus.ACTIVE,
      NOW
    );

    var integrationEvent = mapper.map(metadata, event);

    assertThat(integrationEvent)
      .isExactlyInstanceOf(BankAccountStatusChangedIntegrationEvent.class);

    assertThat(integrationEvent.metadata()).isEqualTo(metadata);
    assertThat(integrationEvent.eventType())
      .isEqualTo(BankAccountStatusChangedIntegrationEvent.EVENT_TYPE);

    var payload = integrationEvent.payload();

    assertThat(payload)
      .extracting(BankAccountStatusChangedIntegrationPayload::bankAccountId)
      .isEqualTo(BANK_ACCOUNT_ID.value());

    assertThat(payload.previousStatus()).isEqualTo(BankAccountStatus.PENDING);
    assertThat(payload.currentStatus()).isEqualTo(BankAccountStatus.ACTIVE);
  }

  @Test
  void shouldReturnBankAccountStatusChangedEventClass_whenDomainEventTypeIsRequested() {
    assertThat(mapper.domainEventType()).isEqualTo(BankAccountStatusChangedEvent.class);
  }
}
