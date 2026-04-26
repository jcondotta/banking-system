package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.domain.exception.DomainNotFoundException;
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
public class ResourceNotFoundExceptionHandler {

  static final String TITLE_RESOURCE_NOT_FOUND = "Not Found";

  @ExceptionHandler(RecipientNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleRecipientNotFound(RecipientNotFoundException ex, HttpServletRequest request) {
    log.atWarn()
      .setMessage("Recipient was not found")
      .addKeyValue("event", "recipient_not_found")
      .addKeyValue("outcome", "failure")
      .addKeyValue("reason", "not_found")
      .addKeyValue("httpStatus", 404)
      .addKeyValue("path", request.getRequestURI())
      .addKeyValue("recipientId", ex.getRecipientId())
      .log();

    return ResponseEntity.of(buildNotFoundProblemDetail(ex, request)).build();
  }

  @ExceptionHandler(DomainNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleResourceNotFound(DomainNotFoundException ex, HttpServletRequest request) {
    log.atWarn()
      .setMessage("Resource was not found")
      .addKeyValue("event", "resource_not_found")
      .addKeyValue("outcome", "failure")
      .addKeyValue("reason", "not_found")
      .addKeyValue("httpStatus", 404)
      .addKeyValue("path", request.getRequestURI())
      .addKeyValue("exceptionType", ex.getClass().getSimpleName())
      .log();

    return ResponseEntity.of(buildNotFoundProblemDetail(ex, request)).build();
  }

  private ProblemDetail buildNotFoundProblemDetail(DomainNotFoundException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problemDetail.setType(ProblemTypes.RESOURCE_NOT_FOUND);
    problemDetail.setTitle(TITLE_RESOURCE_NOT_FOUND);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return problemDetail;
  }
}
