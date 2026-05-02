package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping("${app.api.recipients.root-path}")
public interface ListRecipientsController {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, version = "1.0")
  ResponseEntity<ListRecipientsResponse> listRecipients(
    @PathVariable("bank-account-id") UUID bankAccountId,
    @RequestParam(defaultValue = "0") @Min(0) int page,
    @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
  );
}
