package com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging;

import com.jcondotta.banking.infrastructure.adapters.input.messaging.EventMessage;
import com.jcondotta.banking.recipients.application.bank_account.command.register.RegisterActivatedBankAccountCommand;
import com.jcondotta.banking.recipients.application.bank_account.command.register.RegisterActivatedBankAccountCommandHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountActivatedEventConsumerTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private RegisterActivatedBankAccountCommandHandler commandHandler;

  @Captor
  private ArgumentCaptor<RegisterActivatedBankAccountCommand> commandCaptor;

  @Test
  void shouldRegisterBankAccount_whenActivatedEventIsConsumed() {
    var message = new EventMessage<>(
      UUID.randomUUID().toString(),
      UUID.randomUUID(),
      BANK_ACCOUNT_ID.toString(),
      "bank-account-activated",
      "accounts",
      Instant.parse("2026-09-17T10:00:00Z"),
      1,
      new BankAccountActivatedData("ES3801283316232166447417", "EUR")
    );
    when(objectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(message);
    var consumer = new BankAccountActivatedEventConsumer(objectMapper, commandHandler);

    consumer.consume("{}".getBytes());

    verify(commandHandler).handle(commandCaptor.capture());
    assertThat(commandCaptor.getValue().bankAccountId().value()).isEqualTo(BANK_ACCOUNT_ID);
  }
}
