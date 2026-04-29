package com.jcondotta.domain.exception;

public final class InvalidDomainDataException extends DomainValidationException {

  public InvalidDomainDataException(String message) {
    super(message);
  }
}
