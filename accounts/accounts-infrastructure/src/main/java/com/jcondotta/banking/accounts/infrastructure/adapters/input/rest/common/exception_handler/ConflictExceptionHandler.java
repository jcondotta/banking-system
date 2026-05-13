package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainConflictException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ConflictExceptionHandler {

  static final HttpStatus HTTP_STATUS_CONFLICT = HttpStatus.CONFLICT;
  public static final String TITLE_RESOURCE_ALREADY_EXISTS = "Resource already exists";

  @ExceptionHandler(DomainConflictException.class)
  public ResponseEntity<ProblemDetail> handleConflict(DomainConflictException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_CONFLICT);
    problemDetail.setType(ProblemTypes.CONFLICT);
    problemDetail.setTitle(TITLE_RESOURCE_ALREADY_EXISTS);
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }
}
