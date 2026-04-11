package com.jcondotta.banking.infrastructure.adapters.config.aws.dynamodb;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class DynamoDbEnhancedClientConfig {

  @Bean
  @ConditionalOnBean(DynamoDbClient.class)
  public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
    return DynamoDbEnhancedClient
      .builder()
      .dynamoDbClient(dynamoDbClient)
      .build();
  }
}