package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.get_recipient;

import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.model.RecipientRestResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("${app.api.recipients.recipient-id-path}")
public interface GetRecipientController {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, version = "1.0")
  ResponseEntity<RecipientRestResponse> getRecipient(
    @PathVariable("bank-account-id") UUID bankAccountId,
    @PathVariable("recipient-id") UUID recipientId
  );
}
