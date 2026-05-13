package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.domain.exception.DomainRuleViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleViolationExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/accounts/bank-account-id";
  private static final String EXCEPTION_MESSAGE = "Bank account does not belong to bank account";

  private final RuleViolationExceptionHandler handler = new RuleViolationExceptionHandler();

  @Test
  void shouldReturnRuleViolationProblemDetail_whenDomainRuleViolationExceptionIsThrown() {
    var exception = new TestDomainRuleViolationException(EXCEPTION_MESSAGE);
    var request = mock(HttpServletRequest.class);

    when(request.getRequestURI()).thenReturn(REQUEST_URI);

    var response = handler.handleRuleViolation(exception, request);
    var problemDetail = response.getBody();

    assertAll(
      () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT),
      () -> assertThat(problemDetail).isNotNull(),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(RuleViolationExceptionHandler.HTTP_STATUS_UNPROCESSABLE_CONTENT.value()),
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.RULE_VIOLATION),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(RuleViolationExceptionHandler.TITLE_OPERATION_NOT_ALLOWED),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(EXCEPTION_MESSAGE),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  private static final class TestDomainRuleViolationException extends DomainRuleViolationException {

    private TestDomainRuleViolationException(String message) {
      super(message);
    }
  }
}
