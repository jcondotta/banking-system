package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.remove_recipient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("${app.api.v1.recipients.recipient-id-path}")
public interface RemoveRecipientController {

  @DeleteMapping
  ResponseEntity<Void> removeRecipient(
    @PathVariable("bank-account-id") UUID bankAccountId,
    @PathVariable("recipient-id") UUID recipientId
  );
}
