package com.jcondotta.banking.accounts.config.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxTableProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Configuration
public class OutboxDynamoConfiguration {

  @Bean
  public DynamoDbTable<OutboxEntity> outboxTable(OutboxTableProperties tableProperties, DynamoDbEnhancedClient enhancedClient) {
    return enhancedClient.table(tableProperties.tableName(), TableSchema.fromBean(OutboxEntity.class));
  }
}
