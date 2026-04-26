package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQuery;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQueryResult;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.mapper.ListRecipientsRestMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
public class ListRecipientsControllerImpl implements ListRecipientsController {

  private final QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> queryHandler;
  private final ListRecipientsRestMapper mapper;

  @Override
  public ResponseEntity<ListRecipientsResponse> listRecipients(UUID bankAccountId) {
    var query = mapper.toQuery(bankAccountId);
    ListRecipientsQueryResult queryResult = queryHandler.handle(query);

    log.atInfo()
      .setMessage("Recipients listed")
      .addKeyValue("event", "recipients_listed")
      .addKeyValue("operation", "list")
      .addKeyValue("outcome", "success")
      .addKeyValue("httpStatus", 200)
      .addKeyValue("bankAccountId", bankAccountId)
      .addKeyValue("resultCount", queryResult.recipients().size())
      .log();

    return ResponseEntity.ok(ListRecipientsResponse.from(queryResult));
  }
}