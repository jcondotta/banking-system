package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.accounts.infrastructure.config.ClockTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.identity.EventId;

import java.time.Instant;
import java.util.UUID;

class DefaultDomainEventToIntegrationEventResolverTest {

  private static final UUID CORRELATION_ID = UUID.randomUUID();
  private static final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
  private static final AccountHolderId ACCOUNT_HOLDER_ID = AccountHolderId.newId();

  private static final Currency CURRENCY_EUR = Currency.EUR;
  private static final AccountType ACCOUNT_TYPE_CHECKING = AccountType.CHECKING;

//  @Test
//  void shouldDelegateToCorrectMapper_whenMapperExistsForDomainEvent() {
//
//    var mapper = new TestOpenedMapper();
//    var resolver = new DefaultDomainEventToIntegrationEventEventResolver(List.of(mapper));
//
//    DomainEvent event = new BankAccountOpenedEvent(
//      EVENT_ID,
//      BANK_ACCOUNT_ID,
//      ACCOUNT_TYPE_CHECKING,
//      CURRENCY_EUR,
//      ACCOUNT_HOLDER_ID,
//      NOW
//    );
//
//    resolver.toIntegrationEvent(event, CORRELATION_ID);
//
//    assertThat(mapper.invoked).isTrue();
//  }
//
//  @Test
//  void shouldThrowDomainEventMapperNotFoundException_whenNoMapperIsRegisteredForEvent() {
//
//    var resolver = new DefaultDomainEventToIntegrationEventEventResolver(List.of());
//
//    DomainEvent event =
//      new BankAccountClosedEvent(EVENT_ID, BANK_ACCOUNT_ID, NOW);
//
//    assertThatThrownBy(() ->
//      resolver.toIntegrationEvent(event, CORRELATION_ID)
//    ).isInstanceOf(DomainEventMapperNotFoundException.class);
//  }
//
//  @Test
//  void shouldThrowDuplicateDomainEventMapperException_whenDuplicateMappersAreRegistered() {
//
//    var mapper1 = new TestOpenedMapper();
//    var mapper2 = new TestOpenedMapper();
//
//    assertThatThrownBy(() ->
//      new DefaultDomainEventToIntegrationEventEventResolver(List.of(mapper1, mapper2))
//    ).isInstanceOf(DuplicateDomainEventMapperException.class);
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenMapperListIsNull() {
//
//    assertThatThrownBy(() ->
//      new DefaultDomainEventToIntegrationEventEventResolver(null)
//    ).isInstanceOf(NullPointerException.class)
//      .hasMessage("domainEventMappers must not be null");
//  }
//
//  @Test
//  @SuppressWarnings("all")
//  void shouldThrowNullPointerException_whenMapperInsideListIsNull() {
//
////    assertThatThrownBy(() ->
////      new DefaultDomainEventToIntegrationEventResolver(List.of((DomainEventMapper) null))
////    ).isInstanceOf(NullPointerException.class)
////      .hasMessage("domainEventMapper must not be null");
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenMappedEventTypeIsNull() {
//
//    DomainEventMapper invalidMapper = new DomainEventMapper() {
//      @Override
//      public Class<? extends DomainEvent> mappedEventType() {
//        return null;
//      }
//
//      @Override
//      public IntegrationEvent<?> toIntegrationEvent(DomainEvent event, EventMetadataContext eventMetadataContext) {
//        return null;
//      }
//    };
//
//    assertThatThrownBy(() ->
//      new DefaultDomainEventToIntegrationEventEventResolver(List.of(invalidMapper))
//    ).isInstanceOf(NullPointerException.class)
//      .hasMessage("mappedEventType must not be null");
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenEventIsNull() {
//
//    var resolver = new DefaultDomainEventToIntegrationEventEventResolver(List.of());
//
//    assertThatThrownBy(() ->
//      resolver.toIntegrationEvent(null, CORRELATION_ID)
//    ).isInstanceOf(NullPointerException.class)
//      .hasMessage("event must not be null");
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenCorrelationIdIsNull() {
//
//    var mapper = new TestOpenedMapper();
//    var resolver = new DefaultDomainEventToIntegrationEventEventResolver(List.of(mapper));
//
//    DomainEvent event = new BankAccountOpenedEvent(
//      EVENT_ID,
//      BANK_ACCOUNT_ID,
//      ACCOUNT_TYPE_CHECKING,
//      CURRENCY_EUR,
//      ACCOUNT_HOLDER_ID,
//      NOW
//    );
//
//    assertThatThrownBy(() ->
//      resolver.toIntegrationEvent(event, null)
//    ).isInstanceOf(NullPointerException.class)
//      .hasMessage("correlationId must not be null");
//  }
//
//  private static class TestOpenedMapper implements DomainEventMapper {
//
//    private boolean invoked = false;
//
//    @Override
//    public Class<? extends DomainEvent> mappedEventType() {
//      return BankAccountOpenedEvent.class;
//    }
//
//    @Override
//    public IntegrationEvent<?> toIntegrationEvent(DomainEvent event, UUID correlationId) {
//      invoked = true;
//
//      return new IntegrationEvent<>() {
//        @Override
//        public Object payload() {
//          return null;
//        }
//
//        @Override
//        public IntegrationEventMetadata metadata() {
//          return null;
//        }
//      };
//    }
//  }
}