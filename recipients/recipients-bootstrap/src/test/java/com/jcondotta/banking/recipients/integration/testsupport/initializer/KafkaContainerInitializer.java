package com.jcondotta.banking.recipients.integration.testsupport.initializer;

import com.jcondotta.banking.recipients.integration.testsupport.container.KafkaContainerSupport;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class KafkaContainerInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(
            "KAFKA_BOOTSTRAP_SERVERS=" + KafkaContainerSupport.bootstrapServers(),
            "KAFKA_BANK_ACCOUNT_OPENED_TOPIC_NAME=bank-account-opened",
            "KAFKA_BANK_ACCOUNT_ACTIVATED_TOPIC_NAME=bank-account-activated",
            "KAFKA_BANK_ACCOUNT_BLOCKED_TOPIC_NAME=bank-account-blocked",
            "KAFKA_BANK_ACCOUNT_UNBLOCKED_TOPIC_NAME=bank-account-unblocked",
            "KAFKA_BANK_ACCOUNT_CLOSED_TOPIC_NAME=bank-account-closed",
            "KAFKA_JOINT_ACCOUNT_HOLDER_ADDED_TOPIC_NAME=joint-account-holder-added"
        ).applyTo(ctx.getEnvironment());
    }
}