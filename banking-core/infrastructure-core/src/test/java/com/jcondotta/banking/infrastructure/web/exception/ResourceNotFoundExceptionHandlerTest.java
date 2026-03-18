package com.jcondotta.banking.infrastructure.web.exception;

import com.jcondotta.banking.infrastructure.web.problem.ProblemTypes;
import com.jcondotta.domain.exception.DomainNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResourceNotFoundExceptionHandlerTest {

  private static final String MESSAGE = "resource not found";
  private static final String REQUEST_URI = "/test";

  private final ResourceNotFoundExceptionHandler handler = new ResourceNotFoundExceptionHandler();

  private static class TestDomainNotFoundException extends DomainNotFoundException {
    protected TestDomainNotFoundException(String message) {
      super(message);
    }
  }

  @Test
  void shouldReturnProblemDetail_whenDomainNotFoundExceptionIsHandled() {
    var exception = new TestDomainNotFoundException(MESSAGE);
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleResourceNotFound(exception, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    assertThat(response.getBody())
      .isNotNull()
      .satisfies(body -> {
        assertThat(body.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(body.getType()).isEqualTo(ProblemTypes.RESOURCE_NOT_FOUND);
        assertThat(body.getTitle()).isEqualTo(ResourceNotFoundExceptionHandler.TITLE_RESOURCE_NOT_FOUND);
        assertThat(body.getDetail()).isEqualTo(MESSAGE);
        assertThat(body.getInstance()).isEqualTo(URI.create(REQUEST_URI));
        assertThat(body.getProperties()).isNull();
      });
  }
}