package com.jcondotta.banking.accounts.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws.dynamodb.tables.bank-accounts")
public record BankAccountsTableProperties(String tableName) {

}