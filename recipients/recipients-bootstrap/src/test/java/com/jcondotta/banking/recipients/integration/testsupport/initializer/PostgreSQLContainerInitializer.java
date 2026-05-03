package com.jcondotta.banking.recipients.integration.testsupport.initializer;

import com.jcondotta.banking.recipients.integration.testsupport.container.PostgreSQLContainerSupport;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class PostgreSQLContainerInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(
            "TEST_DATASOURCE_URL=" + appendJdbcParameters(PostgreSQLContainerSupport.jdbcUrl()),
            "TEST_DATASOURCE_USERNAME=" + PostgreSQLContainerSupport.username(),
            "TEST_DATASOURCE_PASSWORD=" + PostgreSQLContainerSupport.password()
        ).applyTo(ctx.getEnvironment());
    }

    private static String appendJdbcParameters(String jdbcUrl) {
        var separator = jdbcUrl.contains("?") ? "&" : "?";
        return jdbcUrl + separator + "connectTimeout=1&socketTimeout=1";
    }
}
