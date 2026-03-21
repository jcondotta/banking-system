package com.jcondotta.domain.exception;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class DomainValidationExceptionTest {

  private static final String EXCEPTION_MESSAGE = "validation failed";

  @Test
  void shouldReturnMessage_whenMessageIsPassedToConstructor() {
    var exception = new DomainValidationException(EXCEPTION_MESSAGE);

    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }

  @Test
  void shouldExtendDomainException_whenInstantiated() {
    var exception = new DomainValidationException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainException.class);
  }

  @Test
  void shouldNotBeAbstract_whenInspectedViaReflection() {
    assertThat(Modifier.isAbstract(DomainValidationException.class.getModifiers()))
      .isFalse();
  }

  @Test
  void shouldBeInstantiable_whenConstructedDirectly() {
    var exception = new DomainValidationException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainValidationException.class);
    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }
}