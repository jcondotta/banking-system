package com.jcondotta.banking.recipients.integration.testsupport.initializer;

import com.jcondotta.banking.recipients.integration.testsupport.container.LocalStackContainerSupport;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class LocalStackContainerInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext ctx) {
        TestPropertyValues.of(
            "AWS_ACCESS_KEY_ID=" + LocalStackContainerSupport.accessKey(),
            "AWS_SECRET_ACCESS_KEY=" + LocalStackContainerSupport.secretKey(),
            "AWS_DEFAULT_REGION=" + LocalStackContainerSupport.region(),
            "AWS_DYNAMODB_ENDPOINT=" + LocalStackContainerSupport.dynamoEndpoint()
        ).applyTo(ctx.getEnvironment());
    }
}