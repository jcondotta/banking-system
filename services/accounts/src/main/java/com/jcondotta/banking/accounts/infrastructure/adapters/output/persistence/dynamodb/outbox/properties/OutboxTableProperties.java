package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws.dynamodb.tables.outbox")
public record OutboxTableProperties(String tableName, Indexes indexes) {

  public record Indexes(Index gsi1) {
  }

  public record Index(String name) {
  }
}
