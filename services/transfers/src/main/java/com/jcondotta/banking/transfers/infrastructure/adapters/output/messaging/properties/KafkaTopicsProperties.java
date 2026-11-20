package com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicsProperties(
    TopicConfig internalTransferRequested,
    TopicConfig internalTransferCompleted
) {

    public record TopicConfig(String topicName) {}
}
