package com.jcondotta.banking.infrastructure.web.exception;

import com.jcondotta.banking.infrastructure.web.problem.ProblemTypes;
import com.jcondotta.domain.exception.DomainRuleValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleValidationExceptionHandlerTest {

  private static final String MESSAGE = "error message";
  private static final String REQUEST_URI = "/test";

  private final RuleValidationExceptionHandler handler = new RuleValidationExceptionHandler();

  private static class TestDomainRuleValidationException extends DomainRuleValidationException {
    protected TestDomainRuleValidationException(String message) {
      super(message);
    }
  }

  @Test
  void shouldReturnProblemDetail_whenDomainRuleValidationExceptionIsHandled() {
    var exception = new TestDomainRuleValidationException(MESSAGE);
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleBusinessRuleViolation(exception, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

    assertThat(response.getBody())
      .isNotNull()
      .satisfies(body -> {
        assertThat(body.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(body.getType()).isEqualTo(ProblemTypes.RULE_VIOLATION);
        assertThat(body.getTitle()).isEqualTo(RuleValidationExceptionHandler.TITLE_OPERATION_NOT_ALLOWED);
        assertThat(body.getDetail()).isEqualTo(MESSAGE);
        assertThat(body.getInstance()).isEqualTo(URI.create(REQUEST_URI));
        assertThat(body.getProperties()).isNull();
      });
  }
}