package com.jcondotta.banking.recipients.integration.recipient.remove;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.application.common.exception.RecipientOptimisticLockException;
import com.jcondotta.banking.recipients.application.recipient.command.remove.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.application.recipient.command.remove.RemoveRecipientCommandHandler;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOwnershipMismatchException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doThrow;

@IntegrationTest
class RemoveRecipientIT {

  @MockitoSpyBean
  private RemoveRecipientCommandHandler removeRecipientCommandHandler;

  @Autowired
  private RecipientRepository recipientRepository;

  @Autowired
  private AccountRecipientsURIProperties uriProperties;

  private BankAccountId bankAccountId;
  private RequestSpecification requestSpecification;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;

    bankAccountId = BankAccountId.of(UUID.randomUUID());
    requestSpecification = buildRequestSpecification();
  }

  @Test
  void shouldReturn204NoContentAndHardDeleteRecipient_whenRecipientIsRemoved() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(bankAccountId);
    recipientRepository.save(recipient);

    var response = deleteRecipient(bankAccountId.value(), recipient.getId().value());
    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

    assertThat(recipientRepository.findById(recipient.getId())).isEmpty();
  }

  @Test
  void shouldReturn404NotFound_whenRecipientIsAlreadyRemoved() {
    var recipient = RecipientFixtures.PATRIZIO.toRecipient(bankAccountId);
    recipientRepository.save(recipient);

    var firstResponse = deleteRecipient(bankAccountId.value(), recipient.getId().value());
    assertThat(firstResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

    var response = deleteRecipient(bankAccountId.value(), recipient.getId().value());
    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());

    var problemDetail = response.as(ProblemDetail.class);

    assertThat(problemDetail)
      .isNotNull()
      .extracting(ProblemDetail::getDetail)
      .isEqualTo(RecipientNotFoundException.MESSAGE);
  }

  @Test
  void shouldReturn404NotFound_whenRecipientDoesNotExist() {
    var nonExistentRecipientId = UUID.randomUUID();

    var response = deleteRecipient(bankAccountId.value(), nonExistentRecipientId);
    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    var problemDetail = response.as(ProblemDetail.class);

    var expectedMessage = new RecipientNotFoundException(RecipientId.of(nonExistentRecipientId), bankAccountId).getMessage();

    assertAll(
      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.RESOURCE_NOT_FOUND.toString()),
      () -> assertThat(problemDetail.getTitle()).isEqualTo("Not Found"),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessage),
      () -> assertThat(problemDetail.getInstance())
        .isEqualTo(uriProperties.recipientURI(bankAccountId.value(), nonExistentRecipientId))
    );
  }

  @Test
  void shouldReturn422UnprocessableEntity_whenRecipientBelongsToAnotherBankAccount() {
    var recipient = RecipientFixtures.VIRGINIO.toRecipient(bankAccountId);
    var otherBankAccountId = UUID.randomUUID();
    recipientRepository.save(recipient);

    var response = deleteRecipient(otherBankAccountId, recipient.getId().value());
    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());

    var problemDetail = response.as(ProblemDetail.class);

    assertAll(
      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.RULE_VIOLATION.toString()),
      () -> assertThat(problemDetail.getTitle()).isEqualTo("Operation not allowed"),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(RecipientOwnershipMismatchException.MESSAGE),
      () -> assertThat(problemDetail.getInstance())
        .isEqualTo(uriProperties.recipientURI(otherBankAccountId, recipient.getId().value()))
    );
  }

  @Test
  void shouldReturn409Conflict_whenRecipientVersionIsStale() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(bankAccountId);
    recipientRepository.save(recipient);

    var command = new RemoveRecipientCommand(bankAccountId, recipient.getId());
    doThrow(new RecipientOptimisticLockException(recipient.getId()))
      .when(removeRecipientCommandHandler)
      .handle(command);

    var response = deleteRecipient(bankAccountId.value(), recipient.getId().value());
    assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    var problemDetail = response.as(ProblemDetail.class);

    assertAll(
      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.CONFLICT.toString()),
      () -> assertThat(problemDetail.getTitle()).isEqualTo("Resource already exists"),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(RecipientOptimisticLockException.RECIPIENT_CONCURRENT_MODIFICATION),
      () -> assertThat(problemDetail.getInstance())
        .isEqualTo(uriProperties.recipientURI(bankAccountId.value(), recipient.getId().value())),
      () -> assertThat(recipientRepository.findById(recipient.getId())).isPresent()
    );
  }

  private Response deleteRecipient(UUID bankAccountId, UUID recipientId) {
    return given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .pathParam("recipient-id", recipientId)
      .when()
      .delete()
      .then()
      .extract()
      .response();
  }

  private RequestSpecification buildRequestSpecification() {
    return new RequestSpecBuilder()
      .setBaseUri(RestAssured.baseURI)
      .setPort(RestAssured.port)
      .setBasePath(uriProperties.recipientIdPath())
      .build();
  }
}
