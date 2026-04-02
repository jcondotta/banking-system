package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.create_recipient;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient.CreateRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.properties.AccountRecipientsURIProperties;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class CreateRecipientControllerImpl implements CreateRecipientController {

  private final CommandHandlerWithResult<CreateRecipientCommand, RecipientId> commandHandler;
  private final AccountRecipientsURIProperties uriProperties;

  @Override
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Void> createRecipient(UUID bankAccountId, CreateRecipientRestRequest request) {
    var command = new CreateRecipientCommand(
      BankAccountId.of(bankAccountId),
      RecipientName.of(request.recipientName()),
      Iban.of(request.iban())
    );
    var recipientId = commandHandler.handle(command);

    return ResponseEntity
      .created(uriProperties.recipientURI(bankAccountId, recipientId.value()))
      .build();
  }
}
