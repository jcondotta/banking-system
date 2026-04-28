package com.jcondotta.banking.recipients.integration.recipient.create;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.testsupport.BlankValuesSource;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
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
  private RecipientRepository recipientRepository;

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
  void shouldReturn201CreatedAndPersistRecipient_whenRequestIsValid() {
    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

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
    var persistedRecipient = recipientRepository.findById(RecipientId.of(recipientId));

    assertAll(
      () -> assertThat(locationHeader).isEqualTo(uriProperties.recipientURI(bankAccountId, recipientId).toString()),
      () -> assertThat(persistedRecipient).hasValueSatisfying(recipient -> assertAll(
          () -> assertThat(recipient.getId().value()).isEqualTo(recipientId),
          () -> assertThat(recipient.getBankAccountId().value()).isEqualTo(bankAccountId),
          () -> assertThat(recipient.getRecipientName().value()).isEqualTo(recipientName),
          () -> assertThat(recipient.getIban().value()).isEqualTo(iban),
          () -> assertThat(recipient.isPersisted()).isTrue(),
          () -> assertThat(recipient.getCreatedAt()).isNotNull()
        ))
    );
  }

  @Test
  void shouldReturn409Conflict_whenCreatingRecipientWithSameIbanForSameBankAccount() {
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

//    var expectedMessageError = new DuplicateRecipientIbanException(iban).getMessage();
//
//    assertAll(
//      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.CONFLICT.toString()),
//      () -> assertThat(problemDetail.getTitle()).isEqualTo(ConflictExceptionHandler.TITLE_RESOURCE_ALREADY_EXISTS),
//      () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessageError),
//      () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId))
//    );
  }

  @Test
  void shouldReturn201Created_whenRecipientWithSameIbanWasRemoved() {
    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    var firstLocationHeader = given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .body(restRequest)
      .when()
      .post()
      .then()
      .statusCode(HttpStatus.CREATED.value())
      .extract()
      .header(HttpHeaders.LOCATION);
    var firstRecipientId = recipientIdFrom(firstLocationHeader);

    deleteRecipient(firstRecipientId);

    var secondLocationHeader = given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .body(restRequest)
      .when()
      .post()
      .then()
      .statusCode(HttpStatus.CREATED.value())
      .extract()
      .header(HttpHeaders.LOCATION);
    var secondRecipientId = recipientIdFrom(secondLocationHeader);

    assertAll(
      () -> assertThat(secondRecipientId).isNotEqualTo(firstRecipientId),
      () -> assertThat(recipientRepository.findById(RecipientId.of(firstRecipientId))).isEmpty(),
      () -> assertThat(recipientRepository.findById(RecipientId.of(secondRecipientId)))
        .hasValueSatisfying(recipient -> assertThat(recipient.isPersisted()).isTrue())
    );
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldReturn422UnprocessableEntity_whenRecipientNameIsBlank(String invalidRecipientName) {
    var restRequest = new CreateRecipientRestRequest(invalidRecipientName, iban);

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

    assert422ValidationProblem(problemDetail);
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldReturn422UnprocessableEntity_whenIbanIsBlank(String invalidIban) {
    var restRequest = new CreateRecipientRestRequest(recipientName, invalidIban);

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

    assert422ValidationProblem(problemDetail);
  }

  @Test
  void shouldReturn422UnprocessableEntity_whenIbanIsInvalid() {
    var restRequest = new CreateRecipientRestRequest(recipientName, "INVALID123");

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

    assert422ValidationProblem(problemDetail);
  }

  @Test
  void shouldReturn422UnprocessableEntity_whenRecipientNameIsTooLong() {
    var tooLongName = "a".repeat(51);
    var restRequest = new CreateRecipientRestRequest(tooLongName, iban);

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

    assert422ValidationProblem(problemDetail);
  }

  @Test
  void shouldReturn400BadRequest_whenJsonIsMalformed() {
    var problemDetail = given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .body("{ invalid-json }")
      .when()
      .post()
      .then()
      .statusCode(HttpStatus.BAD_REQUEST.value())
      .extract()
      .body()
      .as(ProblemDetail.class);

    assert400ValidationProblem(problemDetail);
  }

  @Test
  void shouldReturn400BadRequest_whenRequestBodyIsMissing() {
    var problemDetail = given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .when()
      .post()
      .then()
      .statusCode(HttpStatus.BAD_REQUEST.value())
      .extract()
      .body()
      .as(ProblemDetail.class);

    assert400ValidationProblem(problemDetail);
  }

  private void assert422ValidationProblem(ProblemDetail problemDetail) {
    assertAll(
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
      () -> assertThat(problemDetail.getTitle()).isEqualTo("Request validation failed"),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value()),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId))
    );
  }

  private void assert400ValidationProblem(ProblemDetail problemDetail) {
    assertAll(
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
      () -> assertThat(problemDetail.getTitle()).isEqualTo("Request validation failed"),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo("Request body could not be read"),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId))
    );
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

  private void deleteRecipient(UUID recipientId) {
    given()
      .baseUri(RestAssured.baseURI)
      .port(RestAssured.port)
      .basePath(uriProperties.recipientIdPath())
      .pathParam("bank-account-id", bankAccountId)
      .pathParam("recipient-id", recipientId)
      .when()
      .delete()
      .then()
      .statusCode(HttpStatus.NO_CONTENT.value());
  }

  private static UUID recipientIdFrom(String locationHeader) {
    return UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf("/") + 1));
  }
}
