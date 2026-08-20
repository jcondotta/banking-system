package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.model;

import jakarta.validation.constraints.NotBlank;

public record CreateRecipientRestRequest(@NotBlank String recipientName, @NotBlank String iban) {

}
