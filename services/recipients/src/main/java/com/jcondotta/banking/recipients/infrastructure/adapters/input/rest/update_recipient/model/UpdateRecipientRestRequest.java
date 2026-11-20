package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.model;

import jakarta.validation.constraints.NotBlank;

public record UpdateRecipientRestRequest(@NotBlank String recipientName, @NotBlank String iban) {

}
