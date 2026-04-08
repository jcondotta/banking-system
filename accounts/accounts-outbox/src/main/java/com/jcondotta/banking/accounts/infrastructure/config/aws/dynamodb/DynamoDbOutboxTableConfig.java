package com.jcondotta.banking.accounts.infrastructure.config.aws.dynamodb;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxTableProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
public class DynamoDbOutboxTableConfig {

  @Bean
  public DynamoDbTable<OutboxEntity> outboxDynamoDbTable(DynamoDbEnhancedClient dynamoDbEnhancedClient, OutboxTableProperties outboxTableProperties) {
    return dynamoDbEnhancedClient.table(
        outboxTableProperties.tableName(), TableSchema.fromBean(OutboxEntity.class)
    );
  }
}
