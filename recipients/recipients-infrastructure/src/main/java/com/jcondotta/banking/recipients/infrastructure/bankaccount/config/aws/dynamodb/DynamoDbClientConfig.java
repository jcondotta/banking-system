package com.jcondotta.banking.recipients.infrastructure.bankaccount.config.aws.dynamodb;

import com.jcondotta.banking.recipients.infrastructure.bankaccount.config.aws.EndpointOverride;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Slf4j
@Configuration
@SuppressWarnings("all")
public class DynamoDbClientConfig {

  @Bean
  public DynamoDbClient dynamoDbClient(
      Region region,
      ObjectProvider<AwsCredentialsProvider> credentialsProvider,
      @Qualifier("dynamoDbEndpointOverride") ObjectProvider<EndpointOverride> endpointOverride
  ) {
    var builder = DynamoDbClient.builder()
        .region(region);

    credentialsProvider.ifAvailable(builder::credentialsProvider);

    endpointOverride.ifAvailable(e -> {
      log.info("Initializing DynamoDbClient with custom endpoint: {}", e.uri());
      builder.endpointOverride(e.uri());
    });

    return builder.build();
  }

  @Bean
  @Qualifier("dynamoDbEndpointOverride")
  @ConditionalOnProperty(name = "cloud.aws.dynamodb.endpoint")
  EndpointOverride dynamoDbEndpoint(@Value("${cloud.aws.dynamodb.endpoint}") String endpoint) {
    return new EndpointOverride(URI.create(endpoint));
  }
}
