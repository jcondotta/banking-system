package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.FieldMessageError;
import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ValidationErrorMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

@RestControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

  private static final String TITLE_VALIDATION_FAILED = "Request validation failed";
  private static final String ERRORS_PROPERTY = "errors";
  private static final HttpStatus UNPROCESSABLE_CONTENT_HTTP_STATUS = HttpStatus.UNPROCESSABLE_CONTENT;

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationException(
    MethodArgumentNotValidException ex, HttpServletRequest request) {

    List<FieldMessageError> fieldErrors = ValidationErrorMapper.map(ex.getBindingResult().getFieldErrors());

    var problemDetail = buildProblemDetail(request, fieldErrors);

    return ResponseEntity.of(problemDetail).build();
  }

  private ProblemDetail buildProblemDetail(HttpServletRequest request, Object errors) {
    var problemDetail = ProblemDetail.forStatus(UNPROCESSABLE_CONTENT_HTTP_STATUS);
    problemDetail.setType(ProblemTypes.VALIDATION_ERRORS);
    problemDetail.setTitle(TITLE_VALIDATION_FAILED);
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    problemDetail.setProperty(ERRORS_PROPERTY, errors);

    return problemDetail;
  }
}