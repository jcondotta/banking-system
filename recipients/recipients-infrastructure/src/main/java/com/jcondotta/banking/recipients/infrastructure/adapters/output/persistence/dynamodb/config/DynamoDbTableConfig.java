package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.config;

import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.properties.AccountRecipientsTableProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
public class DynamoDbTableConfig {

  @Bean
  public DynamoDbTable<AccountRecipientEntity> dynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient, AccountRecipientsTableProperties tableProperties) {
    return dynamoDbEnhancedClient.table(
        tableProperties.tableName(),
        TableSchema.fromBean(AccountRecipientEntity.class)
    );
  }
}
