package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.mapper;

import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsQuery;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ListRecipientsRestMapper {

  public ListRecipientsQuery toQuery(UUID bankAccountId) {
    return new ListRecipientsQuery(BankAccountId.of(bankAccountId));
  }
}
