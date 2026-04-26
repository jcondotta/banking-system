package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.mapper;

import com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient.CreateRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateRecipientRestMapper {

  public CreateRecipientCommand toCommand(UUID bankAccountId, CreateRecipientRestRequest request) {
    return new CreateRecipientCommand(
      BankAccountId.of(bankAccountId),
      RecipientName.of(request.recipientName()),
      Iban.of(request.iban())
    );
  }
}
