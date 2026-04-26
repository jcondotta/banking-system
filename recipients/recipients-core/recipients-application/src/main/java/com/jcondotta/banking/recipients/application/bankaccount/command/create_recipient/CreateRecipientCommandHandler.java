package com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class CreateRecipientCommandHandler implements CommandHandlerWithResult<CreateRecipientCommand, RecipientId> {

  private final RecipientRepository recipientRepository;
  private final Clock clock;

  public CreateRecipientCommandHandler(RecipientRepository recipientRepository, Clock clock) {
    this.recipientRepository = recipientRepository;
    this.clock = clock;
  }

  @Override
  @Observed(
    name = "recipient.create",
    contextualName = "createRecipient",
    lowCardinalityKeyValues = {
      "aggregate", "recipient",
      "operation", "create"
    }
  )
  public RecipientId handle(CreateRecipientCommand command) {
    var recipient = Recipient.create(
      RecipientId.newId(),
      command.bankAccountId(),
      command.recipientName(),
      command.iban(),
      Instant.now(clock)
    );

    recipientRepository.create(recipient);

    return recipient.getId();
  }
}
