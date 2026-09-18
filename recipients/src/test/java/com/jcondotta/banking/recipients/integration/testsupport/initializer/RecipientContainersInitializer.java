package com.jcondotta.banking.recipients.integration.testsupport.initializer;

import com.jcondotta.banking.recipients.integration.testsupport.container.RecipientsContainerSupport;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class RecipientContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext ctx) {
    RecipientsContainerSupport.start();
    new PostgreSQLContainerInitializer().initialize(ctx);
    new KafkaContainerInitializer().initialize(ctx);
  }
}
