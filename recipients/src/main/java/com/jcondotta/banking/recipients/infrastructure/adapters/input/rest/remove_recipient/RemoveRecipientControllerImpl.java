package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.application.recipient.command.remove.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient.mapper.RemoveRecipientRestMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class RemoveRecipientControllerImpl implements RemoveRecipientController {

  private final CommandHandler<RemoveRecipientCommand> commandHandler;
  private final RemoveRecipientRestMapper mapper;

  @Override
  public ResponseEntity<Void> removeRecipient(UUID bankAccountId, UUID recipientId) {
    var command = mapper.toCommand(bankAccountId, recipientId);
    commandHandler.handle(command);

    return ResponseEntity.noContent().build();
  }
}
