package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class RemoveRecipientControllerImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.of(UUID.randomUUID());

  @Mock
  private CommandHandler<RemoveRecipientCommand> commandHandler;

  @Captor
  private ArgumentCaptor<RemoveRecipientCommand> commandCaptor;

  private RemoveRecipientControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new RemoveRecipientControllerImpl(commandHandler);
  }

  @Test
  void shouldReturn204NoContent_whenRecipientIsRemoved() {
    ResponseEntity<Void> response = controller.removeRecipient(
      BANK_ACCOUNT_ID.value(),
      RECIPIENT_ID.value()
    );

    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getBody()).isNull();

    verify(commandHandler).handle(commandCaptor.capture());

    var capturedCommand = commandCaptor.getValue();
    assertThat(capturedCommand.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(capturedCommand.recipientId()).isEqualTo(RECIPIENT_ID);

    verifyNoMoreInteractions(commandHandler);
  }
}
