package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.application.recipient.command.update.UpdateRecipientCommand;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.mapper.UpdateRecipientRestMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.model.UpdateRecipientRestRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class UpdateRecipientControllerImpl implements UpdateRecipientController {

  private final CommandHandler<UpdateRecipientCommand> commandHandler;
  private final UpdateRecipientRestMapper mapper;

  @Override
  public ResponseEntity<Void> updateRecipient(UUID bankAccountId, UUID recipientId, UpdateRecipientRestRequest request) {
    var command = mapper.toCommand(bankAccountId, recipientId, request);
    commandHandler.handle(command);

    return ResponseEntity.noContent().build();
  }
}
