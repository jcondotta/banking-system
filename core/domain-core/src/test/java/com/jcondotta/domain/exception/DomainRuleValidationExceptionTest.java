package com.jcondotta.domain.exception;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.assertj.core.api.Assertions.assertThat;

class DomainRuleValidationExceptionTest {

  private static final String EXCEPTION_MESSAGE = "rule validation failed";

  private static class TestDomainRuleValidationException extends DomainRuleValidationException {
    public TestDomainRuleValidationException(String message) {
      super(message);
    }
  }

  @Test
  void shouldReturnMessage_whenMessageIsPassedToConstructor() {
    var exception = new TestDomainRuleValidationException(EXCEPTION_MESSAGE);

    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }

  @Test
  void shouldExtendDomainException_whenInstantiatedViaConcreteSubclass() {
    var exception = new TestDomainRuleValidationException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainException.class);
  }

  @Test
  void shouldBeAbstract_whenInspectedViaReflection() {
    assertThat(Modifier.isAbstract(DomainRuleValidationException.class.getModifiers()))
      .isTrue();
  }

  @Test
  void shouldBeInstantiable_whenExtendedByConcreteSubclass() {
    var exception = new TestDomainRuleValidationException(EXCEPTION_MESSAGE);

    assertThat(exception).isInstanceOf(DomainRuleValidationException.class);
    assertThat(exception.getMessage()).isEqualTo(EXCEPTION_MESSAGE);
  }
}