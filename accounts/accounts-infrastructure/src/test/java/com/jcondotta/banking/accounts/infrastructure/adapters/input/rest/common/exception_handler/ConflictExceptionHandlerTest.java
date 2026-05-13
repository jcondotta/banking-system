package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainConflictException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConflictExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/accounts";
  private static final String EXCEPTION_MESSAGE = "Bank account IBAN already exists";

  private final ConflictExceptionHandler handler = new ConflictExceptionHandler();

  @Test
  void shouldReturnConflictProblemDetail_whenDomainConflictExceptionIsThrown() {
    var exception = new TestDomainConflictException(EXCEPTION_MESSAGE);
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleConflict(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(ConflictExceptionHandler.HTTP_STATUS_CONFLICT.value()),
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.CONFLICT),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(ConflictExceptionHandler.TITLE_RESOURCE_ALREADY_EXISTS),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(EXCEPTION_MESSAGE),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  private static final class TestDomainConflictException extends DomainConflictException {

    private TestDomainConflictException(String message) {
      super(message);
    }
  }
}
