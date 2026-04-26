package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient.CreateRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.mapper.CreateRecipientRestMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
public class CreateRecipientControllerImpl implements CreateRecipientController {

  private final CommandHandlerWithResult<CreateRecipientCommand, RecipientId> commandHandler;
  private final AccountRecipientsURIProperties uriProperties;
  private final CreateRecipientRestMapper mapper;

  @Override
  public ResponseEntity<Void> createRecipient(UUID bankAccountId, CreateRecipientRestRequest request) {
    var command = mapper.toCommand(bankAccountId, request);
    var recipientId = commandHandler.handle(command);

    log.atInfo()
      .setMessage("Recipient created")
      .addKeyValue("event", "recipient_creation")
      .addKeyValue("operation", "create")
      .addKeyValue("outcome", "success")
      .addKeyValue("httpStatus", 201)
      .addKeyValue("bankAccountId", bankAccountId)
      .addKeyValue("recipientId", recipientId.value())
      .log();

    return ResponseEntity
      .created(uriProperties.recipientURI(bankAccountId, recipientId.value()))
      .build();
  }
}
