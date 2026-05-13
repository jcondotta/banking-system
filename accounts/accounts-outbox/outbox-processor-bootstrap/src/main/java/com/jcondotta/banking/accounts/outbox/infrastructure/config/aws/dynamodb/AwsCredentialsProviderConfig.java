package com.jcondotta.banking.accounts.outbox.infrastructure.config.aws.dynamodb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration
public class AwsCredentialsProviderConfig {

  @Bean
  public AwsCredentialsProvider staticCredentialsProvider(
      @Value("${cloud.aws.credentials.access-key-id}") String accessKey,
      @Value("${cloud.aws.credentials.secret-key}") String secretKey) {

    return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
  }
}
