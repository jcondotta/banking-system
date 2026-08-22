package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.mapper;

import com.jcondotta.banking.recipients.application.recipient.command.update.UpdateRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.model.UpdateRecipientRestRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateRecipientRestMapper {

  public UpdateRecipientCommand toCommand(UUID bankAccountId, UUID recipientId, UpdateRecipientRestRequest request) {
    return new UpdateRecipientCommand(
      BankAccountId.of(bankAccountId),
      RecipientId.of(recipientId),
      RecipientName.of(request.recipientName()),
      Iban.of(request.iban())
    );
  }
}
