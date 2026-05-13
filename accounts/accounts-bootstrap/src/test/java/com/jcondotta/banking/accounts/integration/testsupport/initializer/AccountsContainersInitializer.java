package com.jcondotta.banking.accounts.integration.testsupport.initializer;

import com.jcondotta.banking.accounts.integration.testsupport.container.AccountsContainerSupport;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class AccountsContainersInitializer
  implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext ctx) {
    AccountsContainerSupport.start();
    new LocalStackContainerInitializer().initialize(ctx);
    new KafkaContainerInitializer().initialize(ctx);
  }
}
