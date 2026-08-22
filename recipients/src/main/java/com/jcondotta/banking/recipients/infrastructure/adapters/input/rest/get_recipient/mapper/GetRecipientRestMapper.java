package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.get_recipient.mapper;

import com.jcondotta.banking.recipients.application.recipient.query.get.GetRecipientQuery;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetRecipientRestMapper {

  public GetRecipientQuery toQuery(UUID bankAccountId, UUID recipientId) {
    return new GetRecipientQuery(
      BankAccountId.of(bankAccountId),
      RecipientId.of(recipientId)
    );
  }
}
