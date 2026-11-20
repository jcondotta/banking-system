package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicsProperties(
    TopicConfig bankAccountOpened,
    TopicConfig bankAccountStatusChanged,
    TopicConfig jointAccountHolderAdded,
    TopicConfig bankAccountActivated
) {

  public record TopicConfig(String topicName) {}
}
