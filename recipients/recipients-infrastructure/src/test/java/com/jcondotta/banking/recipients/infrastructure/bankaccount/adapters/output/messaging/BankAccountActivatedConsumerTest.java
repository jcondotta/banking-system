package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.messaging;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankAccountActivatedConsumerTest {

//  @Mock
//  private RegisterBankAccountCommandHandler handler;
//
//  @Mock
//  private ObjectMapper objectMapper;
//
//  @InjectMocks
//  private BankAccountActivatedConsumer consumer;
//
//  private final UUID bankAccountId = UUID.randomUUID();
//
//  private final EventSourceProvider eventSourceProvider = () -> "bank-account-service";
//
//  @Test
//  void shouldHandleCommand_whenEventIsConsumed() {
//    var metadata = new IntegrationEventMetadata(
//      UUID.randomUUID(),
//      UUID.randomUUID(),
//      eventSourceProvider.get(),
//      1,
//      Instant.now()
//    );
//
//    var payload = new BankAccountActivatedIntegrationPayload(bankAccountId);
//    var event = new BankAccountActivatedIntegrationEvent(metadata, payload);
//
//    consumer.consume(event.toString());
//
//    verify(handler).handle(
//      argThat(command -> command.bankAccountId().value().equals(bankAccountId))
//    );
//  }
}