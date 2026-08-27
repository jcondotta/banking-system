package com.jcondotta.banking.infrastructure.adapters.input.rest.exceptionhandler;

import com.jcondotta.banking.infrastructure.adapters.input.rest.problem.ApiProblem;
import com.jcondotta.banking.infrastructure.adapters.input.rest.problem.FieldErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodArgumentNotValidExceptionHandler {

  public static final String TITLE_VALIDATION_FAILED = "Request validation failed";
  static final String ERRORS_PROPERTY = "errors";

  static final HttpStatus HTTP_STATUS_UNPROCESSABLE_CONTENT = HttpStatus.UNPROCESSABLE_CONTENT;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handle(MethodArgumentNotValidException ex, HttpServletRequest request) {
    var fieldErrors = mapFieldErrors(ex.getBindingResult().getFieldErrors());

    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_UNPROCESSABLE_CONTENT);
    problemDetail.setType(ApiProblem.VALIDATION_ERRORS);
    problemDetail.setTitle(TITLE_VALIDATION_FAILED);
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty(ERRORS_PROPERTY, fieldErrors);

    return ResponseEntity.of(problemDetail).build();
  }

  private static List<FieldErrorDetail> mapFieldErrors(List<FieldError> errors) {
    return errors.stream()
      .collect(Collectors.groupingBy(FieldError::getField, Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())))
      .entrySet()
      .stream()
      .map(e -> new FieldErrorDetail(e.getKey(), e.getValue()))
      .toList();
  }
}
