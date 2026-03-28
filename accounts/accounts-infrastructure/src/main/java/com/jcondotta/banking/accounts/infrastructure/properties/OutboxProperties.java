package com.jcondotta.banking.accounts.infrastructure.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.outbox")
public record OutboxProperties(

  @NotNull @Valid ShardsProperties shards,
  @NotNull @Valid ProcessingProperties processing,
  @NotNull @Valid PollingProperties polling

) {}
