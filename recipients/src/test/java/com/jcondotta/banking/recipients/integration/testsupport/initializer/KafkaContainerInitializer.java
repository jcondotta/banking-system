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
      "KAFKA_BOOTSTRAP_SERVERS=" + KafkaContainerSupport.bootstrapServers()
    ).applyTo(ctx.getEnvironment());
  }
}
