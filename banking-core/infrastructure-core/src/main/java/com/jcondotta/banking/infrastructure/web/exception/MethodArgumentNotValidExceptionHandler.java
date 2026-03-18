package com.jcondotta.banking.infrastructure.web.exception;

import com.jcondotta.banking.infrastructure.web.problem.ProblemTypes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

  static final String TITLE_VALIDATION_FAILED = "Request validation failed";
  static final String ERRORS_PROPERTY = "errors";
  private static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
    var groupedErrors = groupErrorsByField(ex);
    var fieldErrors = toFieldMessageErrors(groupedErrors);

    var problemDetail = buildProblemDetail(request, fieldErrors);
    return ResponseEntity.unprocessableEntity().body(problemDetail);
  }

  private Map<String, List<String>> groupErrorsByField(MethodArgumentNotValidException ex) {
    return ex.getBindingResult().getFieldErrors()
      .stream()
      .collect(Collectors.groupingBy(FieldError::getField,
        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())
      ));
  }

  private List<FieldMessageError> toFieldMessageErrors(Map<String, List<String>> groupedErrors) {
    return groupedErrors.entrySet()
      .stream()
      .map(entry -> FieldMessageError.of(entry.getKey(), entry.getValue()))
      .toList();
  }

  private ProblemDetail buildProblemDetail(HttpServletRequest request, List<FieldMessageError> errors) {
    var problemDetail = ProblemDetail.forStatus(STATUS);
    problemDetail.setType(ProblemTypes.VALIDATION_ERRORS);
    problemDetail.setTitle(TITLE_VALIDATION_FAILED);
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty(ERRORS_PROPERTY, errors);

    return problemDetail;
  }

  private record FieldMessageError(String field, List<String> messages) {
    public static FieldMessageError of(String field, List<String> messages) {
      return new FieldMessageError(field, messages);
    }
  }
}