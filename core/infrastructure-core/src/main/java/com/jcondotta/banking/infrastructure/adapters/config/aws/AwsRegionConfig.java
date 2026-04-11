package com.jcondotta.banking.infrastructure.adapters.config.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Configuration
public class AwsRegionConfig {

  @Bean
  public Region awsRegion(@Value("${cloud.aws.region.static:us-east-1}") String region) {
    return Region.of(region);
  }
}

