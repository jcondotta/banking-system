package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@RestControllerAdvice
public class DomainValidationExceptionHandler {

  private static final String TITLE_VALIDATION_FAILED = "Request validation failed";

  @ExceptionHandler(DomainValidationException.class)
  public ResponseEntity<ProblemDetail> handle(DomainValidationException ex, HttpServletRequest request) {
    log.atWarn()
      .setMessage("Domain validation failed")
      .addKeyValue("event", "domain_validation")
      .addKeyValue("outcome", "failure")
      .addKeyValue("reason", "domain_validation")
      .addKeyValue("detail", ex.getMessage())
      .addKeyValue("httpStatus", 400)
      .addKeyValue("path", request.getRequestURI())
      .addKeyValue("exceptionType", ex.getClass().getSimpleName())
      .log();

    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setType(ProblemTypes.VALIDATION_ERRORS);
    problemDetail.setTitle(TITLE_VALIDATION_FAILED);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.of(problemDetail).build();
  }
}
