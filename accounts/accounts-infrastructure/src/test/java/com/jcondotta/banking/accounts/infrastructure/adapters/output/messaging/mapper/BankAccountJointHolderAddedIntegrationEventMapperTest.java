package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import com.jcondotta.banking.accounts.infrastructure.fixtures.IntegrationEventMetadataFixture;
import com.jcondotta.banking.contracts.addholder.BankAccountJointHolderAddedIntegrationEvent;
import com.jcondotta.banking.contracts.addholder.BankAccountJointHolderAddedIntegrationPayload;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountJointHolderAddedIntegrationEventMapperTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
  private static final AccountHolderId ACCOUNT_HOLDER_ID = AccountHolderId.newId();

  private final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  private final BankAccountJointHolderAddedIntegrationEventMapper mapper = new BankAccountJointHolderAddedIntegrationEventMapper();

  @Test
  void shouldMapToJointAccountHolderAddedIntegrationEvent_whenDomainEventIsValid() {
    var metadata = IntegrationEventMetadataFixture.create();

    var event = new BankAccountJointHolderAddedEvent(
      EVENT_ID,
      BANK_ACCOUNT_ID,
      ACCOUNT_HOLDER_ID,
      NOW
    );

    var integrationEvent = mapper.map(metadata, event);

    assertThat(integrationEvent)
      .isExactlyInstanceOf(BankAccountJointHolderAddedIntegrationEvent.class);

    assertThat(integrationEvent.metadata()).isEqualTo(metadata);
    assertThat(integrationEvent.eventType())
      .isEqualTo(BankAccountJointHolderAddedIntegrationEvent.EVENT_TYPE);

    var payload = integrationEvent.payload();

    assertThat(payload)
      .extracting(
        BankAccountJointHolderAddedIntegrationPayload::bankAccountId,
        BankAccountJointHolderAddedIntegrationPayload::accountHolderId
      )
      .containsExactly(
        BANK_ACCOUNT_ID.value(),
        ACCOUNT_HOLDER_ID.value()
      );
  }

  @Test
  void shouldReturnJointAccountHolderAddedEventClass_whenDomainEventTypeIsRequested() {
    assertThat(mapper.domainEventType()).isEqualTo(BankAccountJointHolderAddedEvent.class);
  }
}