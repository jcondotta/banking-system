package com.jcondotta.banking.accounts.integration.testsupport.initializer;

import com.jcondotta.banking.accounts.integration.testsupport.container.LocalStackContainerSupport;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

public class LocalStackContainerInitializer
  implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext ctx) {
    TestPropertyValues.of(buildProperties()).applyTo(ctx.getEnvironment());
  }

  private static Map<String, String> buildProperties() {
    return Map.of(
      "AWS_ACCESS_KEY_ID", LocalStackContainerSupport.accessKey(),
      "AWS_SECRET_ACCESS_KEY", LocalStackContainerSupport.secretKey(),
      "AWS_DEFAULT_REGION", LocalStackContainerSupport.region(),
      "AWS_DYNAMODB_ENDPOINT", LocalStackContainerSupport.localStackEndpoint(),
      "AWS_DYNAMODB_BANK_ACCOUNTS_TABLE_NAME", "bank-accounts"
    );
  }
}
