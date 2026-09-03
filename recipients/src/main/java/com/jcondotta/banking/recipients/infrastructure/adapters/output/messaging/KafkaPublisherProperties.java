package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.kafka.publisher")
public record KafkaPublisherProperties(Duration publishTimeout) {

  public KafkaPublisherProperties {
    if (publishTimeout == null || publishTimeout.isZero() || publishTimeout.isNegative()) {
      throw new IllegalArgumentException("Kafka publish timeout must be greater than zero");
    }
  }
}
