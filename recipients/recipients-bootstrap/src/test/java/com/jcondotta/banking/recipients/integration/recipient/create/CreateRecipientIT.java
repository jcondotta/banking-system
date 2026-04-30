package com.jcondotta.banking.recipients.integration.recipient.create;

import com.jcondotta.banking.infrastructure.adapters.output.rest.exceptionhandler.ProblemTypes;
import com.jcondotta.banking.recipients.application.recipient.command.remove.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.application.recipient.command.remove.RemoveRecipientCommandHandler;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.BlankValuesSource;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler.ConflictExceptionHandler;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler.TooManyRequestsExceptionHandler;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.exception_handler.MethodArgumentNotValidExceptionHandler;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Clock;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@Slf4j
@IntegrationTest
class CreateRecipientIT {

  @Autowired
  private RecipientRepository recipientRepository;

  @Autowired
  private RemoveRecipientCommandHandler removeRecipientCommandHandler;

  @MockitoSpyBean
  private Clock clock;

  @Autowired
  private AccountRecipientsURIProperties uriProperties;

  @Value("${app.concurrency.recipients.create-limit}")
  private int createRecipientConcurrencyLimit;

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

    var response = postRecipient(bankAccountId, restRequest);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.getHeader(HttpHeaders.CONTENT_TYPE)).isNullOrEmpty();

    var locationHeader = response.getHeader(HttpHeaders.LOCATION);
    var recipientId = recipientIdFrom(locationHeader);
    var persistedRecipient = recipientRepository.findById(RecipientId.of(recipientId));

    assertAll(
      () -> assertThat(locationHeader).isEqualTo(uriProperties.recipientURI(bankAccountId, recipientId).toString()),
      () -> assertThat(persistedRecipient).hasValueSatisfying(recipient -> assertAll(
          () -> assertThat(recipient.getId().value()).isEqualTo(recipientId),
          () -> assertThat(recipient.getBankAccountId().value()).isEqualTo(bankAccountId),
          () -> assertThat(recipient.getRecipientName().value()).isEqualTo(recipientName),
          () -> assertThat(recipient.getIban().value()).isEqualTo(iban),
          () -> assertThat(recipient.isVersioned()).isTrue(),
          () -> assertThat(recipient.getCreatedAt()).isNotNull()
        ))
    );
  }

  @Test
  void shouldReturn201Created_whenRecipientWithSameIbanWasRemoved() {
    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    var response1 = postRecipient(bankAccountId, restRequest);
    var locationHeader1 = response1.getHeader(HttpHeaders.LOCATION);
    var recipientId1 = recipientIdFrom(locationHeader1);

    deleteRecipient(bankAccountId, recipientId1);

    var response2 = postRecipient(bankAccountId, restRequest);
    var locationHeader2 = response2.getHeader(HttpHeaders.LOCATION);
    var recipientId2 = recipientIdFrom(locationHeader2);

    assertAll(
      () -> assertThat(recipientId2).isNotEqualTo(recipientId1),
      () -> assertThat(recipientRepository.findById(RecipientId.of(recipientId1))).isEmpty(),
      () -> assertThat(recipientRepository.findById(RecipientId.of(recipientId2)))
        .hasValueSatisfying(recipient -> assertThat(recipient.isVersioned()).isTrue())
    );
  }

  @Test
  void shouldReturn409Conflict_whenCreatingRecipientWithSameIbanForSameBankAccount() {
    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    var response1 = postRecipient(bankAccountId, restRequest);
    assertThat(response1.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    var conflictResponse = postRecipient(bankAccountId, restRequest);

    assertThat(conflictResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    var problemDetail = conflictResponse.as(ProblemDetail.class);

    assertAll(
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.CONFLICT),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(ConflictExceptionHandler.TITLE_RESOURCE_ALREADY_EXISTS),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(DuplicateRecipientIbanException.MESSAGE),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId))
    );
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldReturn422UnprocessableEntity_whenRecipientNameIsBlank(String invalidRecipientName) {
    var restRequest = new CreateRecipientRestRequest(invalidRecipientName, iban);

    var response = postRecipient(bankAccountId, restRequest);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
    var problemDetail = response.as(ProblemDetail.class);

    assert422ValidationProblem(problemDetail);
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldReturn422UnprocessableEntity_whenIbanIsBlank(String invalidIban) {
    var restRequest = new CreateRecipientRestRequest(recipientName, invalidIban);

    var response = postRecipient(bankAccountId, restRequest);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
    var problemDetail = response.as(ProblemDetail.class);

    assert422ValidationProblem(problemDetail);
  }

  @Test
  void shouldReturn400BadRequest_whenIbanIsInvalid() {
    var restRequest = new CreateRecipientRestRequest(recipientName, "INVALID123");

    var response = postRecipient(bankAccountId, restRequest);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    var problemDetail = response.as(ProblemDetail.class);

    assert400ValidationProblem(problemDetail);
    assertThat(problemDetail.getDetail()).isEqualTo(Iban.IBAN_INVALID_FORMAT);
  }

  @Test
  void shouldReturn400BadRequest_whenRecipientNameIsTooLong() {
    var tooLongName = "a".repeat(51);
    var restRequest = new CreateRecipientRestRequest(tooLongName, iban);

    var response = postRecipient(bankAccountId, restRequest);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    var problemDetail = response.as(ProblemDetail.class);

    assert400ValidationProblem(problemDetail);
    assertThat(problemDetail.getDetail())
      .isEqualTo(RecipientName.NAME_MUST_NOT_EXCEED_LENGTH.formatted(RecipientName.MAX_LENGTH));
  }

  @Test
  void shouldReturn400BadRequest_whenJsonIsMalformed() {
    var response = postRecipient(bankAccountId, "{ invalid-json }");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    var problemDetail = response.as(ProblemDetail.class);

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

  @Test
  void shouldReturnTooManyRequests_whenCreateRecipientConcurrencyLimitIsReached() throws InterruptedException {
    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    var allSlotsOccupied = new CountDownLatch(createRecipientConcurrencyLimit);
    var releaseSlotsLatch = new CountDownLatch(1);

    var counter = new AtomicInteger(0);
    doAnswer(invocationOnMock -> {
      if (counter.incrementAndGet() <= createRecipientConcurrencyLimit) {
        allSlotsOccupied.countDown();
        releaseSlotsLatch.await();
      }
      return invocationOnMock.callRealMethod();
    }).when(clock).instant();

    try (var scope = StructuredTaskScope.open()) {
      var responses = IntStream.range(0, createRecipientConcurrencyLimit)
        .mapToObj(i -> scope.fork(() -> postRecipient(UUID.randomUUID(), restRequest)))
        .toList();

      assertThat(allSlotsOccupied.await(2, TimeUnit.SECONDS))
        .as("all concurrency slots must be occupied before sending the over-limit request")
        .isTrue();

      var rejectedResponse = postRecipient(bankAccountId, restRequest);
      assertThat(rejectedResponse.statusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());

      releaseSlotsLatch.countDown();

      scope.join();

      for (var response : responses) {
        assertThat(response.get().statusCode()).isEqualTo(HttpStatus.CREATED.value());
      }
    }
    finally {
      releaseSlotsLatch.countDown();
      reset(clock);
    }
  }

  private void assert422ValidationProblem(ProblemDetail problemDetail) {
    assertAll(
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(MethodArgumentNotValidExceptionHandler.TITLE_VALIDATION_FAILED),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value()),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId))
    );
  }

  private void assert400ValidationProblem(ProblemDetail problemDetail) {
    assertAll(
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.VALIDATION_ERRORS),
      () -> assertThat(problemDetail.getTitle()).isEqualTo("Request validation failed"),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
      () -> assertThat(problemDetail.getInstance()).isEqualTo(uriProperties.recipientsURI(bankAccountId))
    );
  }

  private void assert429ValidationProblem(ProblemDetail problemDetail) {
    assertAll(
      () -> assertThat(problemDetail.getType()).isEqualTo(ProblemTypes.TOO_MANY_REQUESTS),
      () -> assertThat(problemDetail.getTitle()).isEqualTo(TooManyRequestsExceptionHandler.TITLE_TOO_MANY_REQUESTS),
      () -> assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(TooManyRequestsExceptionHandler.DETAIL_CONCURRENCY_LIMIT_REACHED),
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

  private Response postRecipient(UUID bankAccountId, Object request) {
    return given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .body(request)
      .when()
      .post()
      .then()
      .extract()
      .response();
  }

  private void deleteRecipient(UUID bankAccountId, UUID recipientId) {
    removeRecipientCommandHandler.handle(new RemoveRecipientCommand(
      new BankAccountId(bankAccountId),
      new RecipientId(recipientId)
    ));
  }

  private static UUID recipientIdFrom(String locationHeader) {
    return UUID.fromString(locationHeader.substring(locationHeader.lastIndexOf("/") + 1));
  }
}
