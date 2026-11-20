package com.jcondotta.domain.exception;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionTest {

  private static final String EXCEPTION_MESSAGE = "domain error occurred";
  private static final Throwable EXCEPTION_CAUSE = new RuntimeException("root cause");

  private static class TestDomainException extends DomainException {
    public TestDomainException(String message) {
      super(message);
    }

    public TestDomainException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  @Test
  void shouldReturnMessage_whenMessageIsPassedToConstructor() {
    var exception = new TestDomainException(EXCEPTION_MESSAGE);

    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }

  @Test
  void shouldReturnMessageAndCause_whenMessageAndCauseArePassedToConstructor() {
    var exception = new TestDomainException(EXCEPTION_MESSAGE, EXCEPTION_CAUSE);

    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
    assertThat(exception.getCause()).isEqualTo(EXCEPTION_CAUSE);
  }

  @Test
  void shouldExtendRuntimeException_whenInstantiatedViaConcreteSubclass() {
    var exception = new TestDomainException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(RuntimeException.class);
  }

  @Test
  void shouldBeAbstract_whenInspectedViaReflection() {
    assertThat(Modifier.isAbstract(DomainException.class.getModifiers()))
      .isTrue();
  }

  @Test
  void shouldBeInstantiable_whenExtendedByConcreteSubclass() {
    var exception = new TestDomainException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainException.class);
    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }
}