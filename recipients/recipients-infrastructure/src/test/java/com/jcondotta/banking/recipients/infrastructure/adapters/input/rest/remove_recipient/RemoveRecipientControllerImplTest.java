package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.application.recipient.command.remove.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient.mapper.RemoveRecipientRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveRecipientControllerImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.of(UUID.randomUUID());

  private static final RemoveRecipientCommand COMMAND = new RemoveRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID);

  @Mock
  private CommandHandler<RemoveRecipientCommand> commandHandler;

  @Mock
  private RemoveRecipientRestMapper mapper;

  private RemoveRecipientControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new RemoveRecipientControllerImpl(commandHandler, mapper);
  }

  @Test
  void shouldReturn204NoContent_whenRecipientIsRemoved() {
    when(mapper.toCommand(BANK_ACCOUNT_ID.value(), RECIPIENT_ID.value())).thenReturn(COMMAND);

    ResponseEntity<Void> response = controller.removeRecipient(
      BANK_ACCOUNT_ID.value(),
      RECIPIENT_ID.value()
    );

    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getBody()).isNull();

    verify(mapper).toCommand(BANK_ACCOUNT_ID.value(), RECIPIENT_ID.value());
    verify(commandHandler).handle(COMMAND);
    verifyNoMoreInteractions(mapper, commandHandler);
  }
}
