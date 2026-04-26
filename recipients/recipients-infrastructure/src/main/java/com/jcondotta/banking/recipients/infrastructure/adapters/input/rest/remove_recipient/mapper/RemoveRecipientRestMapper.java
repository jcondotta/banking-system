package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient.mapper;

import com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RemoveRecipientRestMapper {

  public RemoveRecipientCommand toCommand(UUID bankAccountId, UUID recipientId) {
    return new RemoveRecipientCommand(
      BankAccountId.of(bankAccountId),
      RecipientId.of(recipientId)
    );
  }
}
