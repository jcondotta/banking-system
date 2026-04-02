package com.jcondotta.domain.exception;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class DomainNotFoundExceptionTest {

  private static final String EXCEPTION_MESSAGE = "resource not found";

  private static class TestDomainNotFoundException extends DomainNotFoundException {
    public TestDomainNotFoundException(String message) {
      super(message);
    }
  }

  @Test
  void shouldReturnMessage_whenMessageIsPassedToConstructor() {
    var exception = new TestDomainNotFoundException(EXCEPTION_MESSAGE);

    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }

  @Test
  void shouldExtendDomainException_whenInstantiatedViaConcreteSubclass() {
    var exception = new TestDomainNotFoundException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainException.class);
  }

  @Test
  void shouldBeAbstract_whenInspectedViaReflection() {
    assertThat(Modifier.isAbstract(DomainNotFoundException.class.getModifiers()))
      .isTrue();
  }

  @Test
  void shouldBeInstantiable_whenExtendedByConcreteSubclass() {
    var exception = new TestDomainNotFoundException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainNotFoundException.class);
    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }
}