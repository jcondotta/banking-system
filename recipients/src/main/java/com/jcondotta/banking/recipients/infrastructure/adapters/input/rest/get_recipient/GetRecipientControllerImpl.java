package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.get_recipient;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.recipient.query.get.GetRecipientQuery;
import com.jcondotta.banking.recipients.application.recipient.query.get.GetRecipientQueryResult;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.model.RecipientRestResponse;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.get_recipient.mapper.GetRecipientRestMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class GetRecipientControllerImpl implements GetRecipientController {

  private final QueryHandler<GetRecipientQuery, GetRecipientQueryResult> queryHandler;
  private final GetRecipientRestMapper mapper;

  @Override
  public ResponseEntity<RecipientRestResponse> getRecipient(UUID bankAccountId, UUID recipientId) {
    var query = mapper.toQuery(bankAccountId, recipientId);
    var queryResult = queryHandler.handle(query);

    return ResponseEntity.ok(RecipientRestResponse.from(queryResult.recipient()));
  }
}
