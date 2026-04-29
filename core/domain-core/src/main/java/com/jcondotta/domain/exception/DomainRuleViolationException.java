package com.jcondotta.domain.exception;

public abstract class DomainRuleViolationException extends DomainException {

  public DomainRuleViolationException(String message) {
    super(message);
  }
}
