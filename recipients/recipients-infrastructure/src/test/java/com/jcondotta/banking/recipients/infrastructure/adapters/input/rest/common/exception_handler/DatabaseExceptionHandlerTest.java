package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/v1/recipients";

  private final DatabaseExceptionHandler handler = new DatabaseExceptionHandler();

  @Test
  void shouldReturnServiceUnavailableProblemDetail_whenDatabaseIsUnavailable() {
    var exception = new DataAccessResourceFailureException("Database unavailable");
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleDatabaseUnavailable(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(DatabaseExceptionHandler.HTTP_STATUS_SERVICE_UNAVAILABLE.value()),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(DatabaseExceptionHandler.HTTP_STATUS_SERVICE_UNAVAILABLE.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo("Database unavailable"),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  @Test
  void shouldReturnGatewayTimeoutProblemDetail_whenDatabaseTimesOut() {
    var exception = new QueryTimeoutException("Database timeout");
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleDatabaseTimeout(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(DatabaseExceptionHandler.HTTP_STATUS_GATEWAY_TIMEOUT.value()),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(DatabaseExceptionHandler.HTTP_STATUS_GATEWAY_TIMEOUT.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo("Database timeout"),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }
}
