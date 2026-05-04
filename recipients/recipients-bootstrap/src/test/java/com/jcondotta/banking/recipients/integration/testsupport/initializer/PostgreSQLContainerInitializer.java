package com.jcondotta.banking.recipients.integration.testsupport.initializer;

import com.jcondotta.banking.recipients.integration.testsupport.container.PostgreSQLContainerSupport;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

public class PostgreSQLContainerInitializer
  implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(buildProperties()).applyTo(ctx.getEnvironment());
    }

    private static Map<String, String> buildProperties() {
        return Map.of(
          "SPRING_DATASOURCE_URL",      PostgreSQLContainerSupport.jdbcUrl(),
          "SPRING_DATASOURCE_USERNAME", PostgreSQLContainerSupport.username(),
          "SPRING_DATASOURCE_PASSWORD", PostgreSQLContainerSupport.password()
        );
    }
}