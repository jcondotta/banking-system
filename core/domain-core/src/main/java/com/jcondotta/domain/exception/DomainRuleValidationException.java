package com.jcondotta.domain.exception;

public abstract class DomainRuleValidationException extends DomainException {

  public DomainRuleValidationException(String message) {
    super(message);
  }
}
