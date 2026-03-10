package com.jcondotta.banking.recipients.infrastructure.bankaccount.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws.dynamodb.tables.account-recipients")
public record AccountRecipientsTableProperties(String tableName) {
}
