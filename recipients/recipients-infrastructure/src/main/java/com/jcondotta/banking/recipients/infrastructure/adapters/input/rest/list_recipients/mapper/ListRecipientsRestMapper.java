package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.mapper;

import com.jcondotta.application.query.PageRequest;
import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsFilter;
import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsQuery;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.model.ListRecipientsRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ListRecipientsRestMapper {

  public ListRecipientsQuery toQuery(UUID bankAccountId, ListRecipientsRequest request) {
    return new ListRecipientsQuery(BankAccountId.of(bankAccountId), new PageRequest(request.page(), request.size()), toFilter(request.name()));
  }

  private static ListRecipientsFilter toFilter(String name) {
    if (name == null || name.isBlank()) {
      return ListRecipientsFilter.none();
    }

    return ListRecipientsFilter.byName(name.trim());
  }
}
