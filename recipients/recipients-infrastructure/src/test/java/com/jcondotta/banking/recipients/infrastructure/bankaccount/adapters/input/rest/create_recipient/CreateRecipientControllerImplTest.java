package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.create_recipient;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient.CreateRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.properties.AccountRecipientsURIProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRecipientControllerImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final UUID RECIPIENT_UUID = UUID.randomUUID();

  private static final String RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName().value();
  private static final String IBAN = RecipientFixtures.JEFFERSON.toIban().value();

  private static final URI EXPECTED_LOCATION_URI =
      URI.create("https://api.jcondotta.com/v1/bank-accounts/" + BANK_ACCOUNT_UUID + "/recipients/" + RECIPIENT_UUID);

  @Mock
  private CreateRecipientCommand command;

  @Mock
  private CommandHandlerWithResult<CreateRecipientCommand, RecipientId> commandHandler;

  @Mock
  private AccountRecipientsURIProperties uriProperties;

  @Captor
  private ArgumentCaptor<CreateRecipientCommand> commandCaptor;

  private CreateRecipientControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new CreateRecipientControllerImpl(commandHandler, uriProperties);
  }

  @Test
  void shouldCreateRecipientAndReturnCreatedResponse_whenRequestIsValid() {
    var request = new CreateRecipientRestRequest(RECIPIENT_NAME, IBAN);

    when(commandHandler.handle(commandCaptor.capture())).thenReturn(RecipientId.of(RECIPIENT_UUID));
    when(uriProperties.recipientURI(BANK_ACCOUNT_UUID, RECIPIENT_UUID)).thenReturn(EXPECTED_LOCATION_URI);

    ResponseEntity<Void> response = controller.createRecipient(BANK_ACCOUNT_UUID, request);

    assertThat(response.getStatusCode().value()).isEqualTo(201);
    assertThat(response.getHeaders().getLocation()).isEqualTo(EXPECTED_LOCATION_URI);
    assertThat(response.getBody()).isNull();

    verify(commandHandler).handle(any());
    verify(uriProperties).recipientURI(BANK_ACCOUNT_UUID, RECIPIENT_UUID);
//
//    verifyNoMoreInteractions(commandHandler, uriProperties);
  }
}
