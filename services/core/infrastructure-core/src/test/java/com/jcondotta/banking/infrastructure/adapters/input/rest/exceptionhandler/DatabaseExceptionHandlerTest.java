package com.jcondotta.banking.infrastructure.adapters.input.rest.exceptionhandler;

import com.jcondotta.banking.infrastructure.adapters.input.rest.problem.ApiProblem;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.CannotCreateTransactionException;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/resources";

  private final DatabaseExceptionHandler handler = new DatabaseExceptionHandler();

  @Test
  void shouldReturnServiceUnavailableProblemDetail_whenCannotCreateTransactionExceptionIsThrown() {
    var exception = new CannotCreateTransactionException(
      "Could not open transaction",
      new RuntimeException("Could not get database connection")
    );
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleDatabaseUnavailable(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getType()).isEqualTo(ApiProblem.DATABASE_UNAVAILABLE),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(DatabaseExceptionHandler.HTTP_STATUS_SERVICE_UNAVAILABLE.value()),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(DatabaseExceptionHandler.DETAIL_DATABASE_UNAVAILABLE),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  @Test
  void shouldReturnServiceUnavailableProblemDetail_whenDatabaseTimesOut() {
    var exception = new QueryTimeoutException("Database timeout");
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleDatabaseTimeout(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getType()).isEqualTo(ApiProblem.DATABASE_TIMEOUT),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(DatabaseExceptionHandler.HTTP_STATUS_SERVICE_UNAVAILABLE.value()),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(DatabaseExceptionHandler.DETAIL_DATABASE_TIMEOUT),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }
}
