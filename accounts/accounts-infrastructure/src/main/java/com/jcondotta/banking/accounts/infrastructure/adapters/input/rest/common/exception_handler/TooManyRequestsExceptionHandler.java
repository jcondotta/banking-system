package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.resilience.InvocationRejectedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TooManyRequestsExceptionHandler {

  public static final HttpStatus HTTP_STATUS_TOO_MANY_REQUESTS = HttpStatus.TOO_MANY_REQUESTS;

  public static final String DETAIL_CONCURRENCY_LIMIT_REACHED =
    "Request rejected because the concurrency limit was reached";

  @ExceptionHandler(InvocationRejectedException.class)
  public ResponseEntity<ProblemDetail> handle(InvocationRejectedException ex, HttpServletRequest request) {
    var problemDetail = ProblemDetail.forStatus(HTTP_STATUS_TOO_MANY_REQUESTS);
    problemDetail.setType(ProblemTypes.TOO_MANY_REQUESTS);
    problemDetail.setTitle(HTTP_STATUS_TOO_MANY_REQUESTS.getReasonPhrase());
    problemDetail.setDetail(DETAIL_CONCURRENCY_LIMIT_REACHED);
    problemDetail.setInstance(URI.create(request.getRequestURI()));

    return ResponseEntity.of(problemDetail).build();
  }
}
