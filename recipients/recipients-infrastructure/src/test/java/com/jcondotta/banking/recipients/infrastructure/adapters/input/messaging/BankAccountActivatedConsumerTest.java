package com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.banking.accounts.contracts.activate.BankAccountActivatedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.activate.BankAccountActivatedIntegrationPayload;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class BankAccountActivatedConsumerTest {

  @Mock
  private CommandHandler<RegisterBankAccountCommand> commandHandler;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private BankAccountActivatedConsumer consumer;

  private final UUID bankAccountId = UUID.randomUUID();

  @BeforeEach
  void setUp() {
    consumer = new BankAccountActivatedConsumer(commandHandler, objectMapper);
  }

  @Test
  void shouldHandleCommand_whenEventIsConsumed() {
    var metadata = IntegrationEventMetadata.of(UUID.randomUUID(), UUID.randomUUID(), "test-source", Instant.now());
    var payload = new BankAccountActivatedIntegrationPayload(bankAccountId);
    var event = new BankAccountActivatedIntegrationEvent(metadata, payload);

    String json = objectMapper.writeValueAsString(event);
    consumer.consume(json);

    verify(commandHandler).handle(
      argThat(command -> command.bankAccountId().value().equals(bankAccountId))
    );
  }

  @Test
  void shouldThrowIllegalArgumentException_whenMessagePayloadIsInvalid() {
    assertThatThrownBy(() -> consumer.consume("invalid-json"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid message payload")
      .hasCauseInstanceOf(JacksonException.class);

    verifyNoInteractions(commandHandler);
  }
}
