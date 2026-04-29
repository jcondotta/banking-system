package com.jcondotta.domain.exception;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class DomainValidationExceptionTest {

  @Test
  void shouldBeAbstract_whenInspectedViaReflection() {
    assertThat(Modifier.isAbstract(DomainValidationException.class.getModifiers()))
      .isTrue();
  }

  @Test
  @SuppressWarnings("all")
  void shouldExtendDomainException_whenInspectedViaReflection() {
    assertThat(DomainException.class.isAssignableFrom(DomainValidationException.class))
      .isTrue();
  }
}
