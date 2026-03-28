package com.jcondotta.banking.accounts.infrastructure.config.aws.dynamodb;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.properties.BankAccountsTableProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
public class DynamoDbBankingEntityTableConfig {

  @Bean
  public DynamoDbTable<BankingEntity> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient, BankAccountsTableProperties tableProperties) {
    return dynamoDbEnhancedClient.table(
        tableProperties.tableName(),
        TableSchema.fromBean(BankingEntity.class)
    );
  }
}
