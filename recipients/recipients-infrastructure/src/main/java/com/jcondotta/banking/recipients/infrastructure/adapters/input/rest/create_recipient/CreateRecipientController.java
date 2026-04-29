package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient;

import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("${app.api.recipients.root-path}")
public interface CreateRecipientController {

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, version = "1.0")
  ResponseEntity<Void> createRecipient(
      @PathVariable("bank-account-id") UUID bankAccountId,
      @Valid @RequestBody CreateRecipientRestRequest request
  );
}
