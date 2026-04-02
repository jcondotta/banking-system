package com.jcondotta.domain.exception;

public abstract class DomainNotFoundException extends DomainException {

  public DomainNotFoundException(String message) {
    super(message);
  }
}
