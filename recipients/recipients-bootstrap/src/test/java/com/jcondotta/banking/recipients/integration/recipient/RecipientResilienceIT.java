package com.jcondotta.banking.recipients.integration.recipient;

import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.create_recipient.model.CreateRecipientRestRequest;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import com.jcondotta.banking.recipients.integration.testsupport.container.PostgreSQLContainerSupport;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static com.jcondotta.banking.recipients.integration.testsupport.rest.RestAssuredTestConstants.API_VERSION_1;
import static com.jcondotta.banking.recipients.integration.testsupport.rest.RestAssuredTestConstants.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@IntegrationTest
class RecipientResilienceIT {

  private static final String RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName().value();
  private static final String RECIPIENT_IBAN = RecipientFixtures.JEFFERSON.toIban().value();

  @MockitoSpyBean
  private RecipientRepository recipientRepository;

  @Autowired
  private AccountRecipientsURIProperties uriProperties;

  private UUID bankAccountId;

  private RequestSpecification requestSpecification;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    bankAccountId = UUID.randomUUID();

    requestSpecification = new RequestSpecBuilder()
      .setBaseUri(BASE_URI)
      .setPort(port)
      .setBasePath(uriProperties.rootPath())
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .addHeader(HttpHeadersConstants.API_VERSION, API_VERSION_1)
      .build();
  }

  @Test
  @Disabled("This test closes all connections from the Hikari pool.")
  void shouldReturn503ServiceUnavailable_whenPostgresIsUnavailable() {
    try {
      PostgreSQLContainerSupport.pause();

      var response = postRecipient(new CreateRecipientRestRequest(RECIPIENT_NAME, RECIPIENT_IBAN));
      assertThat(response.statusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
    }
    finally {
      PostgreSQLContainerSupport.unpause();
    }
  }

  @Test
  void shouldReturn504GatewayTimeout_whenDatabaseTimesOut() {
    doThrow(new QueryTimeoutException("Database timeout"))
      .when(recipientRepository)
      .save(any(Recipient.class));

    var response = postRecipient(new CreateRecipientRestRequest(RECIPIENT_NAME, RECIPIENT_IBAN));
    assertThat(response.statusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT.value());
  }

  private Response postRecipient(CreateRecipientRestRequest request) {
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
