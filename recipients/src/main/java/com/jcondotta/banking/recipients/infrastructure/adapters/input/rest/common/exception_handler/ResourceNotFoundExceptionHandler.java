package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainNotFoundException;
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
public class ResourceNotFoundExceptionHandler {

  static final HttpStatus HTTP_STATUS_NOT_FOUND = HttpStatus.NOT_FOUND;

  @ExceptionHandler(DomainNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleNotFound(DomainNotFoundException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_NOT_FOUND);
    problemDetail.setType(ProblemTypes.RESOURCE_NOT_FOUND);
    problemDetail.setTitle(HTTP_STATUS_NOT_FOUND.getReasonPhrase());
    problemDetail.setDetail(ex.getMessage());
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }
}
