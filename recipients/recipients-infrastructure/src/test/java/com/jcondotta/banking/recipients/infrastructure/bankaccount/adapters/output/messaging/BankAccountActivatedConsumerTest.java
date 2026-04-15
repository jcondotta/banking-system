package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.messaging;

import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.banking.accounts.contracts.activate.BankAccountActivatedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.activate.BankAccountActivatedIntegrationPayload;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommandHandler;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountActivatedConsumerTest {

  @Mock
  private RegisterBankAccountCommandHandler commandHandler;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private BankAccountActivatedConsumer consumer;

  private final UUID bankAccountId = UUID.randomUUID();

  @Test
  void shouldHandleCommand_whenEventIsConsumed() {
    var metadata = Instancio.create(IntegrationEventMetadata.class);

    var payload = new BankAccountActivatedIntegrationPayload(bankAccountId);
    var event = new BankAccountActivatedIntegrationEvent(metadata, payload);

    when(objectMapper.readValue(anyString(), eq(BankAccountActivatedIntegrationEvent.class)))
      .thenReturn(event);

    consumer.consume(event.toString());

    verify(commandHandler).handle(
      argThat(command -> command.bankAccountId().value().equals(bankAccountId))
    );
  }
}