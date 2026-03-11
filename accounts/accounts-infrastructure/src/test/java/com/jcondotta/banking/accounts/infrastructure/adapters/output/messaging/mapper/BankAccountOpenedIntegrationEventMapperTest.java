package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

class BankAccountOpenedIntegrationEventMapperTest {

//  private static final EventId EVENT_ID = EventId.newId();
//  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
//  private static final AccountHolderId ACCOUNT_HOLDER_ID = AccountHolderId.newId();
//
//  private final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);
//
//  private static final UUID CORRELATION_ID = UUID.randomUUID();
//
//  private final BankAccountOpenedIntegrationEventMapper mapper = new BankAccountOpenedIntegrationEventMapper();
//
//  @Test
//  void shouldMapToBankAccountOpenedIntegrationEvent_whenDomainEventIsBankAccountOpenedEvent() {
//    var bankAccountOpenedEvent = new BankAccountOpenedEvent(
//      EVENT_ID,
//      BANK_ACCOUNT_ID,
//      AccountType.CHECKING,
//      Currency.EUR,
//      ACCOUNT_HOLDER_ID,
//      NOW
//    );
//
//    IntegrationEvent<?> integrationEvent = mapper.toIntegrationEvent(bankAccountOpenedEvent, CORRELATION_ID);
//
//    assertThat(integrationEvent)
//      .isInstanceOfSatisfying(BankAccountOpenedIntegrationEvent.class, event -> {
//        var metadata = event.metadata();
//
//        assertThat(metadata.eventId()).isEqualTo(EVENT_ID.value());
//        assertThat(metadata.correlationId()).isEqualTo(CORRELATION_ID);
//        assertThat(metadata.eventType()).isEqualTo(bankAccountOpenedEvent.eventType().value());
//        assertThat(metadata.version()).isEqualTo(AbstractDomainEventMapper.METADATA_VERSION);
//        assertThat(metadata.occurredAt()).isEqualTo(NOW);
//
//        var payload = event.payload();
//        assertThat(payload.id()).isEqualTo(BANK_ACCOUNT_ID.value());
//        assertThat(payload.accountType()).isEqualTo(AccountType.CHECKING.holderName());
//        assertThat(payload.currency()).isEqualTo(Currency.EUR.holderName());
//        assertThat(payload.accountHolderId()).isEqualTo(ACCOUNT_HOLDER_ID.value());
//      });
//  }
//
//  @Test
//  void shouldReturnMappedEventType() {
//    assertThat(mapper.mappedEventType()).isEqualTo(BankAccountOpenedEvent.class);
//  }
//
//  @Test
//  void shouldThrowClassCastException_whenDomainEventTypeDoesNotMatchMapper() {
//    DomainEvent wrongEvent = new BankAccountClosedEvent(
//      EVENT_ID,
//      BANK_ACCOUNT_ID,
//      NOW
//    );
//
//    assertThatThrownBy(() -> mapper.toIntegrationEvent(wrongEvent, CORRELATION_ID))
//      .isInstanceOf(ClassCastException.class);
//  }
}