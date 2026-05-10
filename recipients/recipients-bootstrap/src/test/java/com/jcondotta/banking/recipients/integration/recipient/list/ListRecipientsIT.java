package com.jcondotta.banking.recipients.integration.recipient.list;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import com.jcondotta.banking.recipients.application.recipient.command.remove.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.BlankValuesSource;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.ListRecipientsResponse;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.RecipientRestResponse;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.model.ListRecipientsRequest;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static com.jcondotta.banking.recipients.integration.testsupport.rest.RestAssuredTestConstants.API_VERSION_1;
import static com.jcondotta.banking.recipients.integration.testsupport.rest.RestAssuredTestConstants.BASE_URI;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ListRecipientsIT {

  private static final Instant BASE_CREATED_AT = TimeFactory.FIXED_INSTANT;
  private static final Instant ONE_MINUTE_LATER_CREATED_AT = TimeFactory.FIXED_INSTANT.plus(1, ChronoUnit.MINUTES);
  private static final Instant TEN_MINUTES_LATER_CREATED_AT = TimeFactory.FIXED_INSTANT.plus(10, ChronoUnit.MINUTES);

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 20;

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
    RestAssured.baseURI = BASE_URI;
    RestAssured.port = port;

    bankAccountId = BankAccountId.of(UUID.randomUUID());
    requestSpecification = buildRequestSpecification();
  }

  @Test
  void shouldReturnRecipientsOrderedByNameAsc_whenBankAccountHasRecipients() {
    var patrizioRecipient = recipient(bankAccountId, RecipientFixtures.PATRIZIO, BASE_CREATED_AT);
    var jeffersonRecipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, ONE_MINUTE_LATER_CREATED_AT);

    var otherBankAccountId = BankAccountId.of(UUID.randomUUID());
    var otherAccountRecipient = recipient(otherBankAccountId, RecipientFixtures.VIRGINIO, TEN_MINUTES_LATER_CREATED_AT);

    saveRecipients(patrizioRecipient, otherAccountRecipient, jeffersonRecipient);

    var request = new ListRecipientsRequest(DEFAULT_PAGE, DEFAULT_SIZE, null);
    var response = getRecipients(bankAccountId.value(), request);
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    var body = response.as(ListRecipientsResponse.class);
    assertThat(body.recipients())
      .extracting(RecipientRestResponse::recipientId)
      .containsExactly(
        jeffersonRecipient.getId().value(),
        patrizioRecipient.getId().value()
      );
  }

  @Test
  void shouldReturnOnlyActiveRecipients_whenBankAccountHasActiveAndRemovedRecipients() {
    var jeffersonRecipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, BASE_CREATED_AT);
    var patrizioRecipient = recipient(bankAccountId, RecipientFixtures.PATRIZIO, ONE_MINUTE_LATER_CREATED_AT);

    saveRecipients(jeffersonRecipient, patrizioRecipient);
    removeRecipientHandler.handle(new RemoveRecipientCommand(bankAccountId, jeffersonRecipient.getId()));

    var request = new ListRecipientsRequest(DEFAULT_PAGE, DEFAULT_SIZE, null);
    var response = getRecipients(bankAccountId.value(), request);
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    var body = response.as(ListRecipientsResponse.class);
    assertThat(body.recipients())
      .singleElement()
      .satisfies(recipient -> assertThat(recipient.recipientId()).isEqualTo(patrizioRecipient.getId().value()));
  }

  @Test
  void shouldReturnEmptyList_whenBankAccountHasNoRecipients() {
    var request = new ListRecipientsRequest(DEFAULT_PAGE, DEFAULT_SIZE, null);
    var response = getRecipients(bankAccountId.value(), request);
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    var body = response.as(ListRecipientsResponse.class);
    assertThat(body.recipients()).isEmpty();
  }

  @Test
  void shouldReturnRecipientsFilteredByName_whenNameFilterIsProvided() {
    var jeffersonRecipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, BASE_CREATED_AT);
    var patrizioRecipient = recipient(bankAccountId, RecipientFixtures.PATRIZIO, ONE_MINUTE_LATER_CREATED_AT);
    var virginioRecipient = recipient(bankAccountId, RecipientFixtures.VIRGINIO, TEN_MINUTES_LATER_CREATED_AT);

    saveRecipients(patrizioRecipient, virginioRecipient, jeffersonRecipient);

    var request = new ListRecipientsRequest(DEFAULT_PAGE, DEFAULT_SIZE, "jef");
    var response = getRecipients(bankAccountId.value(), request);
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    var body = response.as(ListRecipientsResponse.class);
    assertThat(body.recipients())
      .singleElement()
      .satisfies(recipient -> assertThat(recipient.recipientId()).isEqualTo(jeffersonRecipient.getId().value()));
  }

  @Test
  void shouldFilterRecipientsByNameIgnoringCase() {
    var jeffersonRecipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, BASE_CREATED_AT);
    var patrizioRecipient = recipient(bankAccountId, RecipientFixtures.PATRIZIO, ONE_MINUTE_LATER_CREATED_AT);

    saveRecipients(patrizioRecipient, jeffersonRecipient);

    var request = new ListRecipientsRequest(DEFAULT_PAGE, DEFAULT_SIZE, "JEFFERSON");
    var response = getRecipients(bankAccountId.value(), request);
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    var body = response.as(ListRecipientsResponse.class);
    assertThat(body.recipients())
      .singleElement()
      .satisfies(recipient -> assertThat(recipient.recipientId()).isEqualTo(jeffersonRecipient.getId().value()));
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldReturnRecipientsNotApplyingFilterName_whenNameFilterIsBlank(String nameFilter) {
    var patrizioRecipient = recipient(bankAccountId, RecipientFixtures.PATRIZIO, BASE_CREATED_AT);
    var jeffersonRecipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, ONE_MINUTE_LATER_CREATED_AT);

    saveRecipients(patrizioRecipient, jeffersonRecipient);

    var request = new ListRecipientsRequest(DEFAULT_PAGE, DEFAULT_SIZE, nameFilter);
    var response = getRecipients(bankAccountId.value(), request);
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    var body = response.as(ListRecipientsResponse.class);
    assertThat(body.recipients())
      .extracting(RecipientRestResponse::recipientId)
      .containsExactly(
        jeffersonRecipient.getId().value(),
        patrizioRecipient.getId().value()
      );
  }

  @Test
  void shouldKeepPagination_whenNameFilterIsProvided() {
    var jeffersonRecipient = recipient(bankAccountId, RecipientFixtures.JEFFERSON, BASE_CREATED_AT);
    var patrizioRecipient = recipient(bankAccountId, RecipientFixtures.PATRIZIO, ONE_MINUTE_LATER_CREATED_AT);
    var virginioRecipient = recipient(bankAccountId, RecipientFixtures.VIRGINIO, TEN_MINUTES_LATER_CREATED_AT);

    saveRecipients(jeffersonRecipient, patrizioRecipient, virginioRecipient);

    var request = new ListRecipientsRequest(0, 2, "condotta");
    var response = getRecipients(bankAccountId.value(), request);
    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    var body = response.as(ListRecipientsResponse.class);
    assertThat(body.recipients())
      .extracting(RecipientRestResponse::recipientName)
      .containsExactly(jeffersonRecipient.getRecipientName().value(), patrizioRecipient.getRecipientName().value());

    assertThat(body.page()).isZero();
    assertThat(body.size()).isEqualTo(2);
    assertThat(body.totalElements()).isEqualTo(3);
    assertThat(body.totalPages()).isEqualTo(2);
    assertThat(body.hasNext()).isTrue();
    assertThat(body.hasPrevious()).isFalse();
  }

  private Response getRecipients(UUID bankAccountId, ListRecipientsRequest listRecipientsRequest) {
    var request = given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId)
      .queryParam("page", listRecipientsRequest.page())
      .queryParam("size", listRecipientsRequest.size());

    if (listRecipientsRequest.name() != null) {
      request.queryParam("name", listRecipientsRequest.name());
    }

    return request
      .when()
      .get()
      .then()
      .extract()
      .response();
  }

  private void saveRecipients(Recipient... recipients) {
    for (var recipient : recipients) {
      recipientRepository.save(recipient);
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

  private static Recipient recipient(BankAccountId bankAccountId, String recipientName, String iban, Instant createdAt) {
    return Recipient.create(
      RecipientId.newId(),
      bankAccountId,
      RecipientName.of(recipientName),
      Iban.of(iban),
      createdAt
    );
  }

  private static Recipient recipient(BankAccountId bankAccountId, RecipientFixtures fixtures, Instant createdAt) {
    return recipient(bankAccountId, fixtures.toName().value(), fixtures.toIban().value(), createdAt);
  }
}
