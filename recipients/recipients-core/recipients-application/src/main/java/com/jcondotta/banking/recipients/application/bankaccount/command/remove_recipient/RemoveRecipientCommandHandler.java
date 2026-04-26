package com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

@Component
public class RemoveRecipientCommandHandler implements CommandHandler<RemoveRecipientCommand> {

  private final RecipientRepository recipientRepository;

  public RemoveRecipientCommandHandler(RecipientRepository recipientRepository) {
    this.recipientRepository = recipientRepository;
  }

  @Override
  @Observed(
    name = "recipient.remove",
    contextualName = "removeRecipient",
    lowCardinalityKeyValues = {
      "aggregate", "recipient",
      "operation", "remove"
    }
  )
  public void handle(RemoveRecipientCommand command) {
    var recipient = recipientRepository.findById(command.recipientId())
      .orElseThrow(() -> new RecipientNotFoundException(command.recipientId()));

    recipient.assertBelongsTo(command.bankAccountId());

    recipientRepository.delete(recipient);
  }
}
