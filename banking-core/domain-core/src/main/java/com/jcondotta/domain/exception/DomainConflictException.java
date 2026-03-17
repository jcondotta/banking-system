package com.jcondotta.domain.exception;

public abstract class DomainConflictException extends DomainException {

  public DomainConflictException(String message) {
    super(message);
  }
}