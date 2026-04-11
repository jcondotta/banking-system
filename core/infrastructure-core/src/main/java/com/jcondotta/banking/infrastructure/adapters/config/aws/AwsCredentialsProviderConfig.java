package com.jcondotta.banking.infrastructure.adapters.config.aws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration
public class AwsCredentialsProviderConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsCredentialsProviderConfig.class);

  @Bean
  @ConditionalOnProperty(name = {"cloud.aws.credentials.access-key-id", "cloud.aws.credentials.secret-key"})
  public AwsCredentialsProvider staticCredentialsProvider(
      @Value("${cloud.aws.credentials.access-key-id}") String accessKey,
      @Value("${cloud.aws.credentials.secret-key}") String secretKey) {

    LOGGER.atInfo()
        .setMessage("Initializing AWS StaticCredentialsProvider with access key: {}")
        .addArgument(accessKey)
        .log();

    return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
  }
}
