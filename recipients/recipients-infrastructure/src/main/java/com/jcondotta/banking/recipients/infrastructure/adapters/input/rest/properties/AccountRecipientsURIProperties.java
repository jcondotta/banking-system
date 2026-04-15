package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@ConfigurationProperties(prefix = "app.api.v1.recipients")
public record AccountRecipientsURIProperties(
    @NotBlank String rootPath, @NotBlank String recipientIdPath) {

  public AccountRecipientsURIProperties {
    if (!rootPath.contains("{bank-account-id}")) {
      throw new IllegalArgumentException(
          "rootPath must contain the {bank-account-id} placeholder, but was: " + rootPath);
    }
    if (!recipientIdPath.contains("{bank-account-id}")) {
      throw new IllegalArgumentException(
          "recipientIdPath must contain the {bank-account-id} placeholder, but was: " + recipientIdPath);
    }
    if (!recipientIdPath.contains("{recipient-id}")) {
      throw new IllegalArgumentException(
          "recipientIdPath must contain the {recipient-id} placeholder, but was: " + recipientIdPath);
    }
  }

  public URI recipientsURI(UUID bankAccountId) {
    return UriComponentsBuilder
        .fromUriString(rootPath)
        .buildAndExpand(Map.of("bank-account-id", bankAccountId))
        .toUri();
  }

  public URI recipientURI(UUID bankAccountId, UUID recipientId) {
    return UriComponentsBuilder
        .fromUriString(recipientIdPath)
        .buildAndExpand(Map.of("bank-account-id", bankAccountId, "recipient-id", recipientId))
        .toUri();
  }
}
