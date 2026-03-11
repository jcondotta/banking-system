package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

class BankAccountActivatedIntegrationEventMapperTest {

//  private static final EventId EVENT_ID = EventId.newId();
//  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
//  private static final UUID CORRELATION_ID = UUID.randomUUID();
//  private static final Instant NOW =
//    Instant.now(ClockTestFactory.FIXED_CLOCK);
//
//  private final BankAccountActivatedIntegrationEventMapper mapper =
//    new BankAccountActivatedIntegrationEventMapper();
//
//  @Test
//  void shouldMapToBankAccountActivatedIntegrationEvent_whenDomainEventIsBankAccountActivatedEvent() {
//    BankAccountActivatedEvent event =
//      new BankAccountActivatedEvent(
//        EVENT_ID,
//        BANK_ACCOUNT_ID,
//        NOW
//      );
//
//    IntegrationEvent<?> integrationEvent =
//      mapper.toIntegrationEvent(event, CORRELATION_ID);
//
//    assertThat(integrationEvent)
//      .isInstanceOfSatisfying(BankAccountActivatedIntegrationEvent.class, mapped -> {
//
//        var metadata = mapped.metadata();
//
//        assertThat(metadata.eventId()).isEqualTo(EVENT_ID.value());
//        assertThat(metadata.correlationId()).isEqualTo(CORRELATION_ID);
//        assertThat(metadata.eventType()).isEqualTo(event.eventType().value());
//        assertThat(metadata.version())
//          .isEqualTo(AbstractDomainEventMapper.METADATA_VERSION);
//        assertThat(metadata.occurredAt()).isEqualTo(NOW);
//
//        var payload = mapped.payload();
//
//        assertThat(payload.id())
//          .isEqualTo(BANK_ACCOUNT_ID.value());
//      });
//  }
//
//  @Test
//  void shouldReturnMappedEventType() {
//    assertThat(mapper.mappedEventType())
//      .isEqualTo(BankAccountActivatedEvent.class);
//  }
}