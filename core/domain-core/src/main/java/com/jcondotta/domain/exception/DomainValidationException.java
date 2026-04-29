package com.jcondotta.domain.exception;

public abstract class DomainValidationException extends DomainException {

  protected DomainValidationException(String message) {
    super(message);
  }
}
