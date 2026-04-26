package com.jcondotta.banking.recipients.integration.testsupport.initializer;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class RecipientContainersInitializer
  implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext ctx) {
    new PostgreSQLContainerInitializer().initialize(ctx);
  }
}
