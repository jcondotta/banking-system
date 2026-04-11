package com.jcondotta.banking.accounts.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws.dynamodb.tables.outbox")
public record OutboxTableProperties(String tableName) {

}