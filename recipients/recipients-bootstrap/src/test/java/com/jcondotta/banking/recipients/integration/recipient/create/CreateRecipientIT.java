package com.jcondotta.banking.recipients.integration.recipient.create;

import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.BlankValuesSource;
import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientFixtures;
import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommand;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipients;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler.ConflictExceptionHandler;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertAll;

@IntegrationTest
class CreateRecipientIT {

  @Autowired
  private CommandHandler<RegisterBankAccountCommand> registerBankAccountHandler;

  @Autowired
  private BankAccountRepository bankAccountRepository;

  @Autowired
  private AccountRecipientsURIProperties uriProperties;

  private UUID bankAccountId;
  private String recipientName;
  private String iban;

  private RequestSpecification requestSpecification;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;

    bankAccountId = UUID.randomUUID();
    recipientName = RecipientFixtures.JEFFERSON.toName().value();
    iban = RecipientFixtures.JEFFERSON.toIban().value();
    requestSpecification = buildRequestSpecification();

  }

  @Test
  void shouldReturn201Created_whenRequestIsValid() {
    registerBankAccountHandler.handle(
      new RegisterBankAccountCommand(BankAccountId.of(bankAccountId))
    );

    var restRequest = new CreateRecipientRestRequest(recipientName, iban);
    var expectedLocationURI = uriProperties.recipientsURI(bankAccountId).toString();

    var locationHeader = given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .body(restRequest)
      .when()
      .post()
      .then()
      .statusCode(HttpStatus.CREATED.value())
      .header(HttpHeaders.CONTENT_TYPE, emptyOrNullString())
      .extract()
      .header(HttpHeaders.LOCATION);

    var recipientId = UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf("/") + 1));

    assertAll(
      () -> assertThat(locationHeader).isEqualTo(expectedLocationURI + "/" + recipientId),
      () -> {
        var bankAccount = bankAccountRepository.findById(BankAccountId.of(bankAccountId))
          .orElseThrow();

        assertThat(bankAccount.getActiveRecipients())
          .hasSize(1)
          .singleElement()
          .satisfies(recipient -> assertAll(
            () -> assertThat(recipient.getId().value()).isEqualTo(recipientId),
            () -> assertThat(recipient.getRecipientName().value()).isEqualTo(recipientName),
            () -> assertThat(recipient.getIban().value()).isEqualTo(iban),
            () -> assertThat(recipient.isActive()).isTrue()
          ));
      }
    );
  }

  @Test
  void shouldReturn409Conflict_whenCreatingRecipientWithSameIban() {
    registerBankAccountHandler.handle(
      new RegisterBankAccountCommand(BankAccountId.of(bankAccountId))
    );

    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .body(restRequest)
      .when()
      .post()
      .then()
      .statusCode(HttpStatus.CREATED.value());

    var problemDetail = given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .body(restRequest)
      .when()
      .post()
      .then()
      .statusCode(HttpStatus.CONFLICT.value())
      .extract()
      .body()
      .as(ProblemDetail.class);

    var expectedMessageError = new DuplicateRecipientIbanException(iban) // TODO smell
      .getMessage();

    assertAll(
      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.CONFLICT.toString()),
      () -> assertThat(problemDetail.getTitle()).hasToString(ConflictExceptionHandler.TITLE_RESOURCE_ALREADY_EXISTS),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessageError),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId))
    );

    var bankAccount = bankAccountRepository.findById(BankAccountId.of(bankAccountId))
      .orElseThrow();

    assertThat(bankAccount.getActiveRecipients()).hasSize(1);
  }


  @Test
  void shouldReturn422UnprocessableEntity_whenCreateRecipientAndBankAccountIsNotActive() {
    var bankAccount = BankAccount.restore(BankAccountId.of(bankAccountId), AccountStatus.BLOCKED, Recipients.empty());
    bankAccountRepository.save(bankAccount);

    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    var problemDetail = given()
        .spec(requestSpecification)
        .pathParam("bank-account-id", bankAccountId)
        .body(restRequest)
        .when()
        .post()
        .then()
        .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value())
        .extract()
        .body()
        .as(ProblemDetail.class);

    var expectedMessageError = new BankAccountNotActiveException(AccountStatus.BLOCKED)
      .getMessage();

    assertAll(
        () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.RULE_VIOLATION.toString()),
        () -> assertThat(problemDetail.getTitle()).hasToString("Operation not allowed"),
        () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessageError),
        () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId)));
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountIsNotFound() {
    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    var problemDetail =
      given()
        .spec(requestSpecification)
        .pathParam("bank-account-id", bankAccountId)
        .body(restRequest)
        .when()
        .post()
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .extract()
        .body()
        .as(ProblemDetail.class);

    var expectedMessageError = new BankAccountNotFoundException(BankAccountId.of(bankAccountId))
      .getMessage();

    assertAll(
      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.RESOURCE_NOT_FOUND.toString()),
      () -> assertThat(problemDetail.getTitle()).hasToString(HttpStatus.NOT_FOUND.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessageError),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId)));
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldReturn422UnprocessableEntity_whenRecipientNameIsBlank(String invalidRecipientName) {
    var restRequest = new CreateRecipientRestRequest(invalidRecipientName, iban);

    var problemDetail =
      given()
        .spec(requestSpecification)
        .pathParam("bank-account-id", bankAccountId)
        .body(restRequest)
      .when()
        .post()
      .then()
        .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value())
        .extract()
        .body()
        .as(ProblemDetail.class);

    assert422ValidationProblem(problemDetail, bankAccountId);
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldReturn422UnprocessableEntity_whenIbanIsBlank(String invalidIban) {
    var restRequest = new CreateRecipientRestRequest(recipientName, invalidIban);

    var problemDetail =
      given()
        .spec(requestSpecification)
        .pathParam("bank-account-id", bankAccountId)
        .body(restRequest)
      .when()
        .post()
      .then()
        .statusCode(HttpStatus.UNPROCESSABLE_CONTENT.value())
        .extract()
        .body()
        .as(ProblemDetail.class);

    assert422ValidationProblem(problemDetail, bankAccountId);
  }

  @Test
  void shouldReturn400BadRequest_whenJsonIsMalformed() {
    given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .body("{ invalid-json }")
      .when()
      .post()
      .then()
      .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  private void assert422ValidationProblem(ProblemDetail problemDetail, UUID bankAccountId) {
    assertAll(
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
      () -> assertThat(problemDetail.getTitle()).hasToString("Request validation failed"),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(422),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId)));
  }

  private RequestSpecification buildRequestSpecification() {
    return new RequestSpecBuilder()
      .setBaseUri(RestAssured.baseURI)
      .setPort(RestAssured.port)
      .setBasePath(uriProperties.rootPath())
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .build();
  }
}
