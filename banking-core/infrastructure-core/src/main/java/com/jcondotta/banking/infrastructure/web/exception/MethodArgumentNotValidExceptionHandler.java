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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

//  private final Clock clock;

  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    var groupedByField =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.groupingBy(
                    FieldError::getField,
                    Collectors.mapping(
                        DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())));

    var fieldMessageErrors =
        groupedByField.entrySet().stream()
            .map(entry -> FieldMessageError.of(entry.getKey(), entry.getValue()))
            .toList();

    log.warn("Validation error at {} -> {}", request.getRequestURI(), groupedByField);

    var problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
    problemDetail.setType(ProblemTypes.VALIDATION_ERRORS);
    problemDetail.setTitle("Request validation failed");
    problemDetail.setInstance(URI.create(request.getRequestURI()));
//    problemDetail.setProperty("timestamp", ZonedDateTime.now(clock));
    problemDetail.setProperty("errors", fieldMessageErrors);

    return ResponseEntity.unprocessableEntity().body(problemDetail);
  }

  private record FieldMessageError(String field, List<String> messages) {

    public static FieldMessageError of(String field, List<String> messages) {
      return new FieldMessageError(field, messages);
    }
  }
}
