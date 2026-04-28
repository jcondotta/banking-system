package com.jcondotta.banking.recipients.integration.recipient.list;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.application.recipient.command.remove.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.ListRecipientsResponse;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.RecipientRestResponse;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties.AccountRecipientsURIProperties;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ListRecipientsIT {

  private static final Instant FIRST_CREATED_AT = Instant.parse("2026-04-17T08:00:00Z");
  private static final Instant SECOND_CREATED_AT = Instant.parse("2026-04-17T09:00:00Z");
  private static final Instant OTHER_CREATED_AT = Instant.parse("2026-04-17T07:00:00Z");

  private static final String RECIPIENT_NAME_JEFFERSON = RecipientTestData.JEFFERSON.getName();
  private static final String IBAN_JEFFERSON = RecipientTestData.JEFFERSON.getIban();

  private static final String RECIPIENT_NAME_PATRIZIO = RecipientTestData.PATRIZIO.getName();
  private static final String IBAN_PATRIZIO = RecipientTestData.PATRIZIO.getIban();

  private static final String RECIPIENT_NAME_VIRGINIO = RecipientTestData.VIRGINIO.getName();
  private static final String IBAN_VIRGINIO = RecipientTestData.VIRGINIO.getIban();

  @Autowired
  private RecipientRepository recipientRepository;

  @Autowired
  private CommandHandler<RemoveRecipientCommand> removeRecipientHandler;

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
  void shouldReturnRecipientsOrderedByNameAsc_whenBankAccountHasRecipients() {
    // Patrizio created first but Jefferson sorts earlier alphabetically (J < P)
    var patrizioRecipient = recipient(
      bankAccountId,
      RECIPIENT_NAME_PATRIZIO,
      IBAN_PATRIZIO,
      FIRST_CREATED_AT
    );
    var jeffersonRecipient = recipient(
      bankAccountId,
      RECIPIENT_NAME_JEFFERSON,
      IBAN_JEFFERSON,
      SECOND_CREATED_AT
    );
    var otherAccountRecipient = recipient(
      BankAccountId.of(UUID.randomUUID()),
      RECIPIENT_NAME_VIRGINIO,
      IBAN_VIRGINIO,
      OTHER_CREATED_AT
    );

    recipientRepository.save(patrizioRecipient);
    recipientRepository.save(otherAccountRecipient);
    recipientRepository.save(jeffersonRecipient);

    var response = getRecipients(bankAccountId.value(), HttpStatus.OK);

    assertThat(response.recipients())
      .extracting(RecipientRestResponse::recipientId)
      .containsExactly(
        jeffersonRecipient.getId().value(),
        patrizioRecipient.getId().value()
      );
    assertThat(response.recipients())
      .extracting(RecipientRestResponse::bankAccountId)
      .containsOnly(bankAccountId.value());
  }

  @Test
  void shouldReturnOnlyActiveRecipients_whenBankAccountHasActiveAndRemovedRecipients() {
    var activeRecipient = recipient(
      bankAccountId,
      RECIPIENT_NAME_JEFFERSON,
      IBAN_JEFFERSON,
      FIRST_CREATED_AT
    );
    var removedRecipient = recipient(
      bankAccountId,
      RECIPIENT_NAME_PATRIZIO,
      IBAN_PATRIZIO,
      SECOND_CREATED_AT
    );

    recipientRepository.save(activeRecipient);
    recipientRepository.save(removedRecipient);
    removeRecipientHandler.handle(new RemoveRecipientCommand(bankAccountId, removedRecipient.getId()));

    var response = getRecipients(bankAccountId.value(), HttpStatus.OK);

    assertThat(response.recipients())
      .singleElement()
      .satisfies(recipient -> assertThat(recipient.recipientId()).isEqualTo(activeRecipient.getId().value()));
  }

  @Test
  void shouldReturnEmptyList_whenBankAccountHasNoRecipients() {
    var response = getRecipients(bankAccountId.value(), HttpStatus.OK);

    assertThat(response.recipients()).isEmpty();
  }

  private ListRecipientsResponse getRecipients(UUID bankAccountId, HttpStatus expectedStatus) {
    return given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .when()
      .get()
      .then()
      .statusCode(expectedStatus.value())
      .extract()
      .body()
      .as(ListRecipientsResponse.class);
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

  private static Recipient recipient(
    BankAccountId bankAccountId,
    String recipientName,
    String iban,
    Instant createdAt
  ) {
    return Recipient.create(
      RecipientId.newId(),
      bankAccountId,
      RecipientName.of(recipientName),
      Iban.of(iban),
      createdAt
    );
  }
}
