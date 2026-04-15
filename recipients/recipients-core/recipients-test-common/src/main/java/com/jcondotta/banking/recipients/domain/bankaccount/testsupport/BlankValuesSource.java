package com.jcondotta.banking.recipients.domain.bankaccount.testsupport;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ArgumentsSource(BlankValuesArgumentProvider.class)
public @interface BlankValuesSource {
}
