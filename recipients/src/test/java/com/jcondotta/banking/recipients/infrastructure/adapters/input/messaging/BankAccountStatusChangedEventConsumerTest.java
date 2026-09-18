package com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging;

import com.jcondotta.banking.infrastructure.adapters.input.messaging.EventMessage;
import com.jcondotta.banking.recipients.application.bank_account.command.update.UpdateBankAccountStatusCommand;
import com.jcondotta.banking.recipients.application.bank_account.command.update.UpdateBankAccountStatusCommandHandler;
import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
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
class BankAccountStatusChangedEventConsumerTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private UpdateBankAccountStatusCommandHandler commandHandler;

  @Captor
  private ArgumentCaptor<UpdateBankAccountStatusCommand> commandCaptor;

  @Test
  void shouldUpdateBankAccountStatus_whenStatusChangedEventIsConsumed() {
    var message = new EventMessage<>(
      UUID.randomUUID().toString(),
      UUID.randomUUID(),
      BANK_ACCOUNT_ID.toString(),
      "bank-account-status-changed",
      "accounts",
      Instant.parse("2026-09-17T10:00:00Z"),
      1,
      new BankAccountStatusChangedData("ACTIVE", "BLOCKED")
    );
    when(objectMapper.readValue(any(byte[].class), any(TypeReference.class))).thenReturn(message);
    var consumer = new BankAccountStatusChangedEventConsumer(objectMapper, commandHandler);

    consumer.consume("{}".getBytes());

    verify(commandHandler).handle(commandCaptor.capture());
    assertThat(commandCaptor.getValue().bankAccountId().value()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(commandCaptor.getValue().status()).isEqualTo(BankAccountStatus.BLOCKED);
  }
}
