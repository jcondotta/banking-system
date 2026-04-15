package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.FieldMessageError;
import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RestExceptionHandlerTest {

  private static final String REQUEST_URI = "/api/v1/bank-accounts/%s/recipients"
    .formatted(UUID.randomUUID());

  @Test
  void shouldReturnNotFoundProblem_whenDomainNotFoundExceptionIsHandled() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());
    var exception = new BankAccountNotFoundException(bankAccountId);
    var request = request();

    var response = new ResourceNotFoundExceptionHandler()
      .handleResourceNotFound(exception, request);

    assertThat(response.getStatusCode().value()).isEqualTo(404);
    assertThat(response.getBody()).isNotNull();
    assertAll(
      () -> assertThat(response.getBody().getType()).isEqualTo(ProblemTypes.RESOURCE_NOT_FOUND),
      () -> assertThat(response.getBody().getTitle()).isEqualTo(ResourceNotFoundExceptionHandler.TITLE_RESOURCE_NOT_FOUND),
      () -> assertThat(response.getBody().getDetail()).isEqualTo(exception.getMessage()),
      () -> assertThat(response.getBody().getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  @Test
  void shouldReturnConflictProblem_whenDomainConflictExceptionIsHandled() {
    var exception = new DuplicateRecipientIbanException("ES3801283316232166447417");
    var request = request();

    var response = new ConflictExceptionHandler()
      .handleConflict(exception, request);

    assertThat(response.getStatusCode().value()).isEqualTo(409);
    assertThat(response.getBody()).isNotNull();
    assertAll(
      () -> assertThat(response.getBody().getType()).isEqualTo(ProblemTypes.CONFLICT),
      () -> assertThat(response.getBody().getTitle()).isEqualTo(ConflictExceptionHandler.TITLE_RESOURCE_ALREADY_EXISTS),
      () -> assertThat(response.getBody().getDetail()).isEqualTo(exception.getMessage()),
      () -> assertThat(response.getBody().getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  @Test
  void shouldReturnRuleViolationProblem_whenDomainRuleValidationExceptionIsHandled() {
    var exception = new BankAccountNotActiveException(AccountStatus.BLOCKED);
    var request = request();

    var response = new RuleValidationExceptionHandler()
      .handleBusinessRuleViolation(exception, request);

    assertThat(response.getStatusCode().value()).isEqualTo(422);
    assertThat(response.getBody()).isNotNull();
    assertAll(
      () -> assertThat(response.getBody().getType()).isEqualTo(ProblemTypes.RULE_VIOLATION),
      () -> assertThat(response.getBody().getTitle()).isEqualTo(RuleValidationExceptionHandler.TITLE_OPERATION_NOT_ALLOWED),
      () -> assertThat(response.getBody().getDetail()).isEqualTo(exception.getMessage()),
      () -> assertThat(response.getBody().getInstance()).isEqualTo(URI.create(REQUEST_URI))
    );
  }

  @Test
  void shouldReturnValidationProblem_whenMethodArgumentNotValidExceptionIsHandled() throws NoSuchMethodException {
    var exception = methodArgumentNotValidException();
    var request = request();

    var response = new MethodArgumentNotValidExceptionHandler()
      .handleValidationException(exception, request);

    assertThat(response.getStatusCode().value()).isEqualTo(422);
    assertThat(response.getBody()).isNotNull();
    assertAll(
      () -> assertThat(response.getBody().getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
      () -> assertThat(response.getBody().getTitle()).isEqualTo("Request validation failed"),
      () -> assertThat(response.getBody().getInstance()).isEqualTo(URI.create(REQUEST_URI)),
      () -> assertThat(response.getBody().getProperties())
        .containsKey("errors")
    );

    var errors = response.getBody().getProperties().get("errors");
    assertThat(errors).isInstanceOf(List.class);
    assertThat((List<?>) errors)
      .singleElement()
      .isInstanceOfSatisfying(FieldMessageError.class, error -> assertAll(
        () -> assertThat(error.field()).isEqualTo("recipientName"),
        () -> assertThat(error.messages()).containsExactly("must not be blank")
      ));
  }

  @SuppressWarnings("unused")
  private void createRecipient(CreateRecipientRestRequest request) {
  }

  @SuppressWarnings("deprecation")
  private MethodArgumentNotValidException methodArgumentNotValidException() throws NoSuchMethodException {
    Method method = RestExceptionHandlerTest.class.getDeclaredMethod(
      "createRecipient",
      CreateRecipientRestRequest.class
    );
    var methodParameter = new MethodParameter(method, 0);
    var bindingResult = new BeanPropertyBindingResult(
      new CreateRecipientRestRequest(null, "ES3801283316232166447417"),
      "request"
    );
    bindingResult.addError(new FieldError(
      "request",
      "recipientName",
      "must not be blank"
    ));

    return new MethodArgumentNotValidException(methodParameter, bindingResult);
  }

  private static MockHttpServletRequest request() {
    var request = new MockHttpServletRequest();
    request.setRequestURI(REQUEST_URI);
    return request;
  }
}
