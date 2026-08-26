package com.jcondotta.banking.infrastructure.adapters.input.rest.exceptionhandler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
    log.atError()
      .setMessage("Unhandled exception")
      .addKeyValue("method", request.getMethod())
      .addKeyValue("path", request.getRequestURI())
      .setCause(ex)
      .log();

    var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setType(ProblemTypes.INTERNAL_ERROR);
    problemDetail.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.of(problemDetail).build();
  }

  @ExceptionHandler(ErrorResponseException.class)
  public ResponseEntity<ProblemDetail> handleErrorResponseException(ErrorResponseException ex, HttpServletRequest request) {
    var problemDetail = ex.getBody();
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(ex.getStatusCode()).body(problemDetail);
  }
}
