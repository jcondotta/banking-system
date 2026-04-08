package com.jcondotta.banking.accounts.infrastructure.config.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Configuration
public class AwsRegionConfig {

//  @Bean
//  @ConditionalOnProperty(name = "cloud.aws.region.static")
//  public Region awsRegion(@Value("${cloud.aws.region.static}") String region) {
//    return Region.of(region);
//  }
}



