package com.jcondotta.banking.recipients.integration.testsupport.annotation;

import com.jcondotta.banking.recipients.integration.testsupport.initializer.RecipientContainersInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = RecipientContainersInitializer.class)
public @interface IntegrationTest {
}