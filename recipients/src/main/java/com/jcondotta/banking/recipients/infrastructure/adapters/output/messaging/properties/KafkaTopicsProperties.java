package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicsProperties(
    TopicConfig recipientCreated,
    TopicConfig recipientDeleted,
    TopicConfig bankAccountActivated,
    TopicConfig bankAccountStatusChanged
) {

  public record TopicConfig(String topicName) {}
}
