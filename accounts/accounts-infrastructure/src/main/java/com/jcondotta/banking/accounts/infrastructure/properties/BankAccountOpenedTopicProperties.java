package com.jcondotta.banking.accounts.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics.bank-account-opened")
public record BankAccountOpenedTopicProperties(String topicName) {}