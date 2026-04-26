package com.jcondotta.banking.recipients.integration.recipient.remove;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient.RemoveRecipientCommandHandler;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOptimisticLockException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOwnershipMismatchException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
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
    recipientRepository.create(recipient);

    deleteRecipient(bankAccountId.value(), recipient.getId().value(), HttpStatus.NO_CONTENT);

    assertThat(recipientRepository.findById(recipient.getId())).isEmpty();
  }

  @Test
  void shouldReturn404NotFound_whenRecipientIsAlreadyRemoved() {
    var recipient = RecipientFixtures.PATRIZIO.toRecipient(bankAccountId);
    recipientRepository.create(recipient);

    deleteRecipient(bankAccountId.value(), recipient.getId().value(), HttpStatus.NO_CONTENT);

    var problemDetail = deleteRecipient(
      bankAccountId.value(),
      recipient.getId().value(),
      HttpStatus.NOT_FOUND
    );

    var expectedMessage = new RecipientNotFoundException(recipient.getId()).getMessage();
    assertThat(problemDetail)
      .isNotNull()
      .extracting(ProblemDetail::getDetail)
      .isEqualTo(expectedMessage);
  }

  @Test
  void shouldReturn404NotFound_whenRecipientDoesNotExist() {
    var nonExistentRecipientId = UUID.randomUUID();

    var problemDetail = deleteRecipient(
      bankAccountId.value(),
      nonExistentRecipientId,
      HttpStatus.NOT_FOUND
    );

    var expectedMessage = new RecipientNotFoundException(RecipientId.of(nonExistentRecipientId)).getMessage();

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
    recipientRepository.create(recipient);

    var problemDetail = deleteRecipient(
      otherBankAccountId,
      recipient.getId().value(),
      HttpStatus.UNPROCESSABLE_CONTENT
    );

    var expectedMessage = new RecipientOwnershipMismatchException(
      recipient.getId(),
      BankAccountId.of(otherBankAccountId)
    ).getMessage();

    assertAll(
      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.RULE_VIOLATION.toString()),
      () -> assertThat(problemDetail.getTitle()).isEqualTo("Operation not allowed"),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessage),
      () -> assertThat(problemDetail.getInstance())
        .isEqualTo(uriProperties.recipientURI(otherBankAccountId, recipient.getId().value()))
    );
  }

  @Test
  void shouldReturn409Conflict_whenRecipientVersionIsStale() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(bankAccountId);
    recipientRepository.create(recipient);

    var command = new RemoveRecipientCommand(bankAccountId, recipient.getId());
    doThrow(new RecipientOptimisticLockException(recipient.getId()))
      .when(removeRecipientCommandHandler)
      .handle(command);

    var problemDetail = deleteRecipient(
      bankAccountId.value(),
      recipient.getId().value(),
      HttpStatus.CONFLICT
    );

    var expectedMessage = new RecipientOptimisticLockException(recipient.getId()).getMessage();

    assertAll(
      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.CONFLICT.toString()),
      () -> assertThat(problemDetail.getTitle()).isEqualTo("Resource already exists"),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessage),
      () -> assertThat(problemDetail.getInstance())
        .isEqualTo(uriProperties.recipientURI(bankAccountId.value(), recipient.getId().value())),
      () -> assertThat(recipientRepository.findById(recipient.getId())).isPresent()
    );
  }

  private ProblemDetail deleteRecipient(UUID bankAccountId, UUID recipientId, HttpStatus expectedStatus) {
    var response = given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .pathParam("recipient-id", recipientId)
      .when()
      .delete()
      .then()
      .statusCode(expectedStatus.value())
      .extract();

    if (expectedStatus.is2xxSuccessful()) {
      return null;
    }

    return response.body().as(ProblemDetail.class);
  }

  private RequestSpecification buildRequestSpecification() {
    return new RequestSpecBuilder()
      .setBaseUri(RestAssured.baseURI)
      .setPort(RestAssured.port)
      .setBasePath(uriProperties.recipientIdPath())
      .build();
  }
}
