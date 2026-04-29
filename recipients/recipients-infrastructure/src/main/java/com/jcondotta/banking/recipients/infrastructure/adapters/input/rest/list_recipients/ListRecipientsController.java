package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("${app.api.recipients.root-path}")
public interface ListRecipientsController {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, version = "1.0")
  ResponseEntity<ListRecipientsResponse> listRecipients(@PathVariable("bank-account-id") UUID bankAccountId);
}
