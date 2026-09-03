package com.jcondotta.banking.accounts.integration.testsupport.initializer;

import com.jcondotta.banking.accounts.integration.testsupport.container.KafkaContainerSupport;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

public class KafkaContainerInitializer
  implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext ctx) {
    TestPropertyValues.of(buildProperties()).applyTo(ctx.getEnvironment());
  }

  private static Map<String, String> buildProperties() {
    return Map.of(
      "KAFKA_BOOTSTRAP_SERVERS", KafkaContainerSupport.bootstrapServers(),
      "KAFKA_BANK_ACCOUNT_OPENED_TOPIC_NAME", "bank-account-opened",
      "KAFKA_BANK_ACCOUNT_STATUS_CHANGED_TOPIC_NAME", "bank-account-status-changed",
      "KAFKA_JOINT_ACCOUNT_HOLDER_ADDED_TOPIC_NAME", "joint-account-holder-added"
    );
  }
}
