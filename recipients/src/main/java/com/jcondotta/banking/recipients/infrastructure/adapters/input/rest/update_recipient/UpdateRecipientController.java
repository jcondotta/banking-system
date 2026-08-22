package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient;

import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.model.UpdateRecipientRestRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("${app.api.recipients.recipient-id-path}")
public interface UpdateRecipientController {

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, version = "1.0")
  ResponseEntity<Void> updateRecipient(
    @PathVariable("bank-account-id") UUID bankAccountId,
    @PathVariable("recipient-id") UUID recipientId,
    @Valid @RequestBody UpdateRecipientRestRequest request
  );
}
