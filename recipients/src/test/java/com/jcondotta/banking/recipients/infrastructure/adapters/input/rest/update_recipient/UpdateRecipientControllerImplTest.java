package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.application.recipient.command.update.UpdateRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.mapper.UpdateRecipientRestMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.model.UpdateRecipientRestRequest;
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
class UpdateRecipientControllerImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.of(UUID.randomUUID());
  private static final UpdateRecipientRestRequest REQUEST = new UpdateRecipientRestRequest(
    RecipientFixtures.JEFFERSON.toName().value(),
    RecipientFixtures.JEFFERSON.toIban().value()
  );
  private static final UpdateRecipientCommand COMMAND = new UpdateRecipientCommand(
    BANK_ACCOUNT_ID,
    RECIPIENT_ID,
    RecipientFixtures.JEFFERSON.toName(),
    RecipientFixtures.JEFFERSON.toIban()
  );

  @Mock
  private CommandHandler<UpdateRecipientCommand> commandHandler;

  @Mock
  private UpdateRecipientRestMapper mapper;

  private UpdateRecipientControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new UpdateRecipientControllerImpl(commandHandler, mapper);
  }

  @Test
  void shouldReturn204NoContent_whenRecipientIsUpdated() {
    when(mapper.toCommand(BANK_ACCOUNT_ID.value(), RECIPIENT_ID.value(), REQUEST)).thenReturn(COMMAND);

    ResponseEntity<Void> response = controller.updateRecipient(
      BANK_ACCOUNT_ID.value(),
      RECIPIENT_ID.value(),
      REQUEST
    );

    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getBody()).isNull();

    verify(mapper).toCommand(BANK_ACCOUNT_ID.value(), RECIPIENT_ID.value(), REQUEST);
    verify(commandHandler).handle(COMMAND);
    verifyNoMoreInteractions(mapper, commandHandler);
  }
}
