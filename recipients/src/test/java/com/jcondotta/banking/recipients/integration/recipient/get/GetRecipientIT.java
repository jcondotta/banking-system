package com.jcondotta.banking.recipients.integration.recipient.get;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import com.jcondotta.banking.recipients.application.recipient.command.remove.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.common.model.RecipientRestResponse;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.RecipientsURIProperties;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper.IbanMasker;
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
import org.springframework.http.ProblemDetail;

import java.time.Instant;
import java.util.UUID;

import static com.jcondotta.banking.recipients.integration.testsupport.rest.RestAssuredTestConstants.API_VERSION_1;
import static com.jcondotta.banking.recipients.integration.testsupport.rest.RestAssuredTestConstants.BASE_URI;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@IntegrationTest
class GetRecipientIT {

  private static final Instant CREATED_AT = TimeFactory.FIXED_INSTANT;

  @Autowired
  private RecipientRepository recipientRepository;

  @Autowired
  private CommandHandler<RemoveRecipientCommand> removeRecipientHandler;

  @Autowired
  private RecipientsURIProperties uriProperties;

  private BankAccountId bankAccountId;
  private RequestSpecification requestSpecification;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = BASE_URI;
    RestAssured.port = port;

    bankAccountId = BankAccountId.of(UUID.randomUUID());
    requestSpecification = buildRequestSpecification();
  }

  @Test
  void shouldReturnRecipient_whenRecipientBelongsToBankAccount() {
    var recipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, CREATED_AT);
    recipientRepository.save(recipient);

    var response = getRecipient(bankAccountId.value(), recipient.getId().value());

    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    var body = response.as(RecipientRestResponse.class);
    assertAll(
      () -> assertThat(body.recipientId()).isEqualTo(recipient.getId().value()),
      () -> assertThat(body.recipientName()).isEqualTo(recipient.getRecipientName().value()),
      () -> assertThat(body.maskedIban()).isEqualTo(IbanMasker.mask(recipient.getIban().value())),
      () -> assertThat(body.createdAt()).isEqualTo(CREATED_AT)
    );
  }

  @Test
  void shouldReturn404NotFound_whenRecipientDoesNotExist() {
    var response = getRecipient(bankAccountId.value(), UUID.randomUUID());

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    var problemDetail = response.as(ProblemDetail.class);
    assertThat(problemDetail.getDetail()).isEqualTo(RecipientNotFoundException.MESSAGE);
  }

  @Test
  void shouldReturn404NotFound_whenRecipientBelongsToAnotherBankAccount() {
    var recipient = recipient(bankAccountId, RecipientFixtures.PATRIZIO, CREATED_AT);
    recipientRepository.save(recipient);

    var response = getRecipient(UUID.randomUUID(), recipient.getId().value());

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    var problemDetail = response.as(ProblemDetail.class);
    assertThat(problemDetail.getDetail()).isEqualTo(RecipientNotFoundException.MESSAGE);
  }

  @Test
  void shouldReturn404NotFound_whenRecipientWasRemoved() {
    var recipient = recipient(bankAccountId, RecipientFixtures.VIRGINIO, CREATED_AT);
    recipientRepository.save(recipient);
    removeRecipientHandler.handle(new RemoveRecipientCommand(bankAccountId, recipient.getId()));

    var response = getRecipient(bankAccountId.value(), recipient.getId().value());

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
  }

  private Response getRecipient(UUID bankAccountId, UUID recipientId) {
    return given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .pathParam("recipient-id", recipientId)
      .when()
      .get()
      .then()
      .extract()
      .response();
  }

  private RequestSpecification buildRequestSpecification() {
    return new RequestSpecBuilder()
      .setBaseUri(RestAssured.baseURI)
      .setPort(RestAssured.port)
      .setBasePath(uriProperties.recipientIdPath())
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .addHeader(HttpHeadersConstants.API_VERSION, API_VERSION_1)
      .build();
  }

  private static Recipient recipient(BankAccountId bankAccountId, RecipientFixtures fixtures, Instant createdAt) {
    return Recipient.create(
      RecipientId.newId(),
      bankAccountId,
      RecipientName.of(fixtures.toName().value()),
      Iban.of(fixtures.toIban().value()),
      createdAt
    );
  }
}
