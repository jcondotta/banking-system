package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.remove_recipient;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
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

  @Override
  public ResponseEntity<Void> removeRecipient(UUID bankAccountId, UUID recipientId) {
    commandHandler.handle(new RemoveRecipientCommand(
      BankAccountId.of(bankAccountId),
      RecipientId.of(recipientId)
    ));

    return ResponseEntity.noContent().build();
  }
}
