package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient.mapper.RemoveRecipientRestMapper;
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
public class RemoveRecipientControllerImpl implements RemoveRecipientController {

  private final CommandHandler<RemoveRecipientCommand> commandHandler;
  private final RemoveRecipientRestMapper mapper;

  @Override
  public ResponseEntity<Void> removeRecipient(UUID bankAccountId, UUID recipientId) {
    var command = mapper.toCommand(bankAccountId, recipientId);
    commandHandler.handle(command);

    log.atInfo()
      .setMessage("Recipient removed")
      .addKeyValue("event", "recipient_removal")
      .addKeyValue("operation", "remove")
      .addKeyValue("outcome", "success")
      .addKeyValue("httpStatus", 204)
      .addKeyValue("bankAccountId", bankAccountId)
      .addKeyValue("recipientId", recipientId)
      .log();

    return ResponseEntity.noContent().build();
  }
}
