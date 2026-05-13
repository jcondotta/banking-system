package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResourceNotFoundExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/accounts/bank-account-id";
  private static final String EXCEPTION_MESSAGE = "Bank account not found";

  private final ResourceNotFoundExceptionHandler handler = new ResourceNotFoundExceptionHandler();

  @Test
  void shouldReturnNotFoundProblemDetail_whenDomainNotFoundExceptionIsThrown() {
    var exception = new TestDomainNotFoundException(EXCEPTION_MESSAGE);
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleNotFound(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value()),
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.RESOURCE_NOT_FOUND),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(EXCEPTION_MESSAGE),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  private static final class TestDomainNotFoundException extends DomainNotFoundException {

    private TestDomainNotFoundException(String message) {
      super(message);
    }
  }
}
