package com.jcondotta.domain.exception;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class DomainConflictExceptionTest {

  private static final String EXCEPTION_MESSAGE = "resource already exists";

  private static class TestDomainConflictException extends DomainConflictException {
    public TestDomainConflictException(String message) {
      super(message);
    }
  }

  @Test
  void shouldReturnMessage_whenMessageIsPassedToConstructor() {
    var exception = new TestDomainConflictException(EXCEPTION_MESSAGE);

    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }

  @Test
  void shouldExtendDomainException_whenInstantiatedViaConcreteSubclass() {
    var exception = new TestDomainConflictException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainException.class);
  }

  @Test
  void shouldBeAbstract_whenInspectedViaReflection() {
    assertThat(Modifier.isAbstract(DomainConflictException.class.getModifiers()))
      .isTrue();
  }

  @Test
  void shouldBeInstantiable_whenExtendedByConcreteSubclass() {
    var exception = new TestDomainConflictException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainConflictException.class);
    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }
}