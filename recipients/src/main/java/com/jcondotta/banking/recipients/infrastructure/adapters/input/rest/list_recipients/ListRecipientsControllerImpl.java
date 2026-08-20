package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsQuery;
import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsQueryResult;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.mapper.ListRecipientsRestMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.model.ListRecipientsRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class ListRecipientsControllerImpl implements ListRecipientsController {

  private final QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> queryHandler;
  private final ListRecipientsRestMapper mapper;

  @Override
  public ResponseEntity<ListRecipientsResponse> listRecipients(UUID bankAccountId, ListRecipientsRequest request) {
    var query = mapper.toQuery(bankAccountId, request);
    ListRecipientsQueryResult queryResult = queryHandler.handle(query);

    return ResponseEntity.ok(ListRecipientsResponse.from(queryResult));
  }
}
