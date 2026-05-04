package com.jcondotta.banking.recipients.integration.recipient.create;

import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Clock;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static com.jcondotta.banking.recipients.integration.testsupport.rest.RestAssuredTestConstants.API_VERSION_1;
import static com.jcondotta.banking.recipients.integration.testsupport.rest.RestAssuredTestConstants.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;

@IntegrationTest
class CreateRecipientConcurrencyIT {

  private static final String RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName().value();
  private static final String RECIPIENT_IBAN = RecipientFixtures.JEFFERSON.toIban().value();

  @MockitoSpyBean
  private Clock clock;

  @Autowired
  private AccountRecipientsURIProperties uriProperties;

  @Value("${app.concurrency.recipients.create.limit}")
  private int createRecipientConcurrencyLimit;

  private UUID bankAccountId;
  private RequestSpecification requestSpecification;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = BASE_URI;
    RestAssured.port = port;

    bankAccountId = UUID.randomUUID();
    requestSpecification = buildRequestSpecification();
  }

  @Test
  void shouldReturnTooManyRequests_whenCreateRecipientConcurrencyLimitIsReached() throws InterruptedException {
    var restRequest = new CreateRecipientRestRequest(RECIPIENT_NAME, RECIPIENT_IBAN);

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
      var concurrentCreateTasks = IntStream.range(0, createRecipientConcurrencyLimit)
        .mapToObj(i -> scope.fork(() -> postRecipient(UUID.randomUUID(), restRequest)))
        .toList();

      assertThat(allSlotsOccupied.await(2, TimeUnit.SECONDS))
        .as("all concurrency slots must be occupied before sending the over-limit request")
        .isTrue();

      var rejectedResponse = postRecipient(bankAccountId, restRequest);
      assertThat(rejectedResponse.statusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());

      releaseSlotsLatch.countDown();
      scope.join();

      for (var concurrentCreateTask : concurrentCreateTasks) {
        assertThat(concurrentCreateTask.get().statusCode()).isEqualTo(HttpStatus.CREATED.value());
      }
    }
    finally {
      reset(clock);
    }
  }

  private RequestSpecification buildRequestSpecification() {
    return new RequestSpecBuilder()
      .setBaseUri(RestAssured.baseURI)
      .setPort(RestAssured.port)
      .setBasePath(uriProperties.rootPath())
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .addHeader(HttpHeadersConstants.API_VERSION, API_VERSION_1)
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
}
