package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

  private static final String TITLE_RECIPIENT_CONCURRENT_MODIFICATION = "Resource already exists";
  private static final String TITLE_INTERNAL_SERVER_ERROR = "Internal Server Error";

  @ExceptionHandler(RecipientOptimisticLockException.class)
  public ResponseEntity<ProblemDetail> handleOptimisticLock(RecipientOptimisticLockException ex, HttpServletRequest request) {
    log.atWarn()
        .setMessage("Recipient conflict detected")
        .addKeyValue("event", "recipient_conflict")
        .addKeyValue("outcome", "failure")
        .addKeyValue("reason", "optimistic_lock_conflict")
        .addKeyValue("httpStatus", 409)
        .addKeyValue("path", request.getRequestURI())
        .addKeyValue("recipientId", ex.getRecipientId())
        .log();

    var problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    problemDetail.setType(ProblemTypes.CONFLICT);
    problemDetail.setTitle(TITLE_RECIPIENT_CONCURRENT_MODIFICATION);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.of(problemDetail).build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
    log.atError()
        .setMessage("Unexpected error")
        .addKeyValue("event", "unexpected_error")
        .addKeyValue("outcome", "failure")
        .addKeyValue("reason", "internal_error")
        .addKeyValue("httpStatus", 500)
        .addKeyValue("path", request.getRequestURI())
        .addKeyValue("exceptionType", ex.getClass().getSimpleName())
        .setCause(ex)
        .log();

    var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setType(ProblemTypes.INTERNAL_ERROR);
    problemDetail.setTitle(TITLE_INTERNAL_SERVER_ERROR);
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.of(problemDetail).build();
  }
}
