package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.messaging;

import com.jcondotta.application.core.events.EventSourceProvider;
import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.banking.contracts.activate.BankAccountActivatedIntegrationEvent;
import com.jcondotta.banking.contracts.activate.BankAccountActivatedIntegrationPayload;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommandHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
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