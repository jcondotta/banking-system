package com.jcondotta.banking.accounts.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics.joint-account-holder-added")
public record JointAccountHolderAddedTopicProperties(String topicName) {}