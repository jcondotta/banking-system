package com.jcondotta.banking.recipients.integration.recipient.remove;

import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;

@IntegrationTest
class RemoveRecipientConcurrencyIT {

  @MockitoSpyBean
  private RecipientRepository recipientRepository;

  @Autowired
  private AccountRecipientsURIProperties uriProperties;

  @Value("${app.concurrency.recipients.remove.limit}")
  private int removeRecipientConcurrencyLimit;

  private RequestSpecification requestSpecification;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = BASE_URI;
    RestAssured.port = port;

    requestSpecification = buildRequestSpecification();
  }

  @Test
  void shouldReturnTooManyRequests_whenRemoveRecipientConcurrencyLimitIsReached() throws InterruptedException {
    var recipients = IntStream.range(0, removeRecipientConcurrencyLimit + 1)
      .mapToObj(i -> persistRecipientDirectly())
      .toList();

    var recipientsToDelete = recipients.subList(0, removeRecipientConcurrencyLimit);
    var recipientRejectedByConcurrencyLimit = recipients.getLast();

    var allSlotsOccupied = new CountDownLatch(removeRecipientConcurrencyLimit);
    var releaseSlotsLatch = new CountDownLatch(1);

    var counter = new AtomicInteger(0);
    doAnswer(invocationOnMock -> {
      if (counter.incrementAndGet() <= removeRecipientConcurrencyLimit) {
        allSlotsOccupied.countDown();
        releaseSlotsLatch.await();
      }
      return invocationOnMock.callRealMethod();
    }).when(recipientRepository).findById(any(RecipientId.class));

    try (var scope = StructuredTaskScope.open()) {
      var concurrentDeleteTasks = recipientsToDelete.stream()
        .map(recipient -> scope.fork(() -> deleteRecipient(recipient.getBankAccountId().value(), recipient.getId().value())))
        .toList();

      assertThat(allSlotsOccupied.await(2, TimeUnit.SECONDS))
        .as("all concurrency slots must be occupied before sending the over-limit request")
        .isTrue();

      var rejectedResponse = deleteRecipient(
        recipientRejectedByConcurrencyLimit.getBankAccountId().value(),
        recipientRejectedByConcurrencyLimit.getId().value()
      );
      assertThat(rejectedResponse.statusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());

      releaseSlotsLatch.countDown();
      scope.join();

      for (var concurrentDeleteTask : concurrentDeleteTasks) {
        assertThat(concurrentDeleteTask.get().statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
      }
    }
    finally {
      reset(recipientRepository);
    }
  }

  private Recipient persistRecipientDirectly() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(bankAccountId);

    recipientRepository.save(recipient);
    return recipient;
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
      .addHeader(HttpHeadersConstants.API_VERSION, API_VERSION_1)
      .build();
  }
}
