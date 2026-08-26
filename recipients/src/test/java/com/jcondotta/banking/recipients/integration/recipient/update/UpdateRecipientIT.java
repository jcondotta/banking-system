package com.jcondotta.banking.recipients.integration.recipient.update;

import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.BlankValuesSource;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.RecipientsURIProperties;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.model.UpdateRecipientRestRequest;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
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
class UpdateRecipientIT {

  private static final Instant CREATED_AT = TimeFactory.FIXED_INSTANT;

  @Autowired
  private RecipientRepository recipientRepository;

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
  void shouldReturn204NoContentAndPersistRecipientUpdate_whenRequestIsValid() {
    var recipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, CREATED_AT);
    recipientRepository.save(recipient);
    var request = new UpdateRecipientRestRequest(
      RecipientFixtures.PATRIZIO.toName().value(),
      RecipientFixtures.PATRIZIO.toIban().value()
    );

    var response = putRecipient(bankAccountId.value(), recipient.getId().value(), request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    assertThat(response.body().asString()).isEmpty();

    assertThat(recipientRepository.findById(recipient.getId()))
      .hasValueSatisfying(updatedRecipient -> assertAll(
        () -> assertThat(updatedRecipient.getId()).isEqualTo(recipient.getId()),
        () -> assertThat(updatedRecipient.getBankAccountId()).isEqualTo(bankAccountId),
        () -> assertThat(updatedRecipient.getRecipientName().value()).isEqualTo(request.recipientName()),
        () -> assertThat(updatedRecipient.getIban().value()).isEqualTo(request.iban()),
        () -> assertThat(updatedRecipient.getCreatedAt()).isEqualTo(CREATED_AT),
        () -> assertThat(updatedRecipient.isVersioned()).isTrue()
      ));
  }

  @Test
  void shouldReturn204NoContent_whenRequestMatchesCurrentRecipientState() {
    var recipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, CREATED_AT);
    recipientRepository.save(recipient);
    var request = new UpdateRecipientRestRequest(
      recipient.getRecipientName().value(),
      recipient.getIban().value()
    );

    var response = putRecipient(bankAccountId.value(), recipient.getId().value(), request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  @Test
  void shouldReturn404NotFound_whenRecipientDoesNotExist() {
    var request = new UpdateRecipientRestRequest(
      RecipientFixtures.PATRIZIO.toName().value(),
      RecipientFixtures.PATRIZIO.toIban().value()
    );

    var response = putRecipient(bankAccountId.value(), UUID.randomUUID(), request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    var problemDetail = response.as(ProblemDetail.class);
    assertThat(problemDetail.getDetail()).isEqualTo(RecipientNotFoundException.MESSAGE);
  }

  @Test
  void shouldReturn404NotFound_whenRecipientBelongsToAnotherBankAccount() {
    var recipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, CREATED_AT);
    recipientRepository.save(recipient);
    var request = new UpdateRecipientRestRequest(
      RecipientFixtures.PATRIZIO.toName().value(),
      RecipientFixtures.PATRIZIO.toIban().value()
    );

    var response = putRecipient(UUID.randomUUID(), recipient.getId().value(), request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    var problemDetail = response.as(ProblemDetail.class);
    assertThat(problemDetail.getDetail()).isEqualTo(RecipientNotFoundException.MESSAGE);
  }

  @Test
  void shouldReturn409Conflict_whenIbanAlreadyExistsForSameBankAccount() {
    var existingRecipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, CREATED_AT);
    var recipientToUpdate = recipient(bankAccountId, RecipientFixtures.PATRIZIO, CREATED_AT);
    recipientRepository.save(existingRecipient);
    recipientRepository.save(recipientToUpdate);
    var request = new UpdateRecipientRestRequest(
      recipientToUpdate.getRecipientName().value(),
      existingRecipient.getIban().value()
    );

    var response = putRecipient(bankAccountId.value(), recipientToUpdate.getId().value(), request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    var problemDetail = response.as(ProblemDetail.class);
    assertThat(problemDetail.getDetail()).isEqualTo(DuplicateRecipientIbanException.MESSAGE);
  }

  @Test
  void shouldReturn204NoContent_whenSameIbanExistsForAnotherBankAccount() {
    var otherBankAccountId = BankAccountId.of(UUID.randomUUID());
    var existingRecipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, CREATED_AT);
    var recipientToUpdate = recipient(otherBankAccountId, RecipientFixtures.PATRIZIO, CREATED_AT);
    recipientRepository.save(existingRecipient);
    recipientRepository.save(recipientToUpdate);
    var request = new UpdateRecipientRestRequest(
      recipientToUpdate.getRecipientName().value(),
      existingRecipient.getIban().value()
    );

    var response = putRecipient(otherBankAccountId.value(), recipientToUpdate.getId().value(), request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    assertThat(recipientRepository.findById(recipientToUpdate.getId()))
      .hasValueSatisfying(updatedRecipient -> assertThat(updatedRecipient.getIban().value())
        .isEqualTo(existingRecipient.getIban().value()));
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldReturn422UnprocessableEntity_whenRecipientNameIsBlank(String invalidRecipientName) {
    var recipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, CREATED_AT);
    recipientRepository.save(recipient);
    var request = new UpdateRecipientRestRequest(invalidRecipientName, RecipientFixtures.PATRIZIO.toIban().value());

    var response = putRecipient(bankAccountId.value(), recipient.getId().value(), request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldReturn422UnprocessableEntity_whenIbanIsBlank(String invalidIban) {
    var recipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, CREATED_AT);
    recipientRepository.save(recipient);
    var request = new UpdateRecipientRestRequest(RecipientFixtures.PATRIZIO.toName().value(), invalidIban);

    var response = putRecipient(bankAccountId.value(), recipient.getId().value(), request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
  }

  @Test
  void shouldReturn422UnprocessableEntity_whenIbanIsInvalid() {
    var recipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, CREATED_AT);
    recipientRepository.save(recipient);
    var request = new UpdateRecipientRestRequest(RecipientFixtures.PATRIZIO.toName().value(), "INVALID123");

    var response = putRecipient(bankAccountId.value(), recipient.getId().value(), request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
  }

  private Response putRecipient(UUID bankAccountId, UUID recipientId, Object request) {
    return given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .pathParam("recipient-id", recipientId)
      .body(request)
      .when()
      .put()
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
