package com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

public final class ValidationErrorMapper {

  private ValidationErrorMapper() {}

  public static List<FieldMessageError> map(List<FieldError> errors) {
    return errors.stream()
      .collect(Collectors.groupingBy(
        FieldError::getField,
        Collectors.mapping(
          DefaultMessageSourceResolvable::getDefaultMessage,
          Collectors.toList()
        )
      ))
      .entrySet()
      .stream()
      .map(e -> new FieldMessageError(e.getKey(), e.getValue()))
      .toList();
  }
}