package com.jcondotta.domain.exception;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class DomainRuleViolationExceptionTest {

  private static final String EXCEPTION_MESSAGE = "rule violation failed";

  private static class TestDomainRuleViolationException extends DomainRuleViolationException {
    public TestDomainRuleViolationException(String message) {
      super(message);
    }
  }

  @Test
  void shouldReturnMessage_whenMessageIsPassedToConstructor() {
    var exception = new TestDomainRuleViolationException(EXCEPTION_MESSAGE);

    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }

  @Test
  void shouldExtendDomainException_whenInstantiatedViaConcreteSubclass() {
    var exception = new TestDomainRuleViolationException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainException.class);
  }

  @Test
  void shouldBeAbstract_whenInspectedViaReflection() {
    assertThat(Modifier.isAbstract(DomainRuleViolationException.class.getModifiers()))
      .isTrue();
  }

  @Test
  void shouldBeInstantiable_whenExtendedByConcreteSubclass() {
    var exception = new TestDomainRuleViolationException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainRuleViolationException.class);
    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }
}