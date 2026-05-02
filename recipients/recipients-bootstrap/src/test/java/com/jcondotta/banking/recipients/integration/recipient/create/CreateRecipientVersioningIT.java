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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class CreateRecipientVersioningIT {

  @Autowired
  private AccountRecipientsURIProperties uriProperties;

  private UUID bankAccountId;
  private String recipientName;
  private String iban;

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
  }

  @Test
  void shouldReturn201Created_whenApiVersionHeaderIsMissingAndDefaultVersionIsUsed() {
    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    var requestSpecification = buildRequestSpecification();
    var response = postRecipient(bankAccountId, restRequest, requestSpecification);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
  }

  @Test
  void shouldReturn201Created_whenApiVersionHeaderIsSupported() {
    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    var requestSpecification = buildRequestSpecification("1.0");
    var response = postRecipient(bankAccountId, restRequest, requestSpecification);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
  }

  @Test
  void shouldReturn400BadRequest_whenApiVersionHeaderIsInvalid() {
    var restRequest = new CreateRecipientRestRequest(recipientName, iban);

    var requestSpecification = buildRequestSpecification("0.1");
    var response = postRecipient(bankAccountId, restRequest, requestSpecification);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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

  private RequestSpecification buildRequestSpecification(String apiVersion) {
    return new RequestSpecBuilder()
      .addRequestSpecification(buildRequestSpecification())
      .addHeader(HttpHeadersConstants.API_VERSION, apiVersion)
      .build();
  }

  private Response postRecipient(UUID bankAccountId, Object request, RequestSpecification specification) {
    return given()
      .spec(specification)
      .pathParam("bank-account-id", bankAccountId)
      .body(request)
      .when()
      .post()
      .then()
      .extract()
      .response();
  }
}
