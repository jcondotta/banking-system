package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws.dynamodb.tables.account-recipients")
public record AccountRecipientsTableProperties(String tableName) {
}
