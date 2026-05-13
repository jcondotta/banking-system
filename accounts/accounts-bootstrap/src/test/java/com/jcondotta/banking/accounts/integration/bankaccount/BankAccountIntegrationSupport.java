package com.jcondotta.banking.accounts.integration.bankaccount;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.BankAccountDetailsResponse;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.properties.BankAccountsURIProperties;
import com.jcondotta.banking.infrastructure.adapters.output.rest.HttpHeadersConstants;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;
import java.util.UUID;

import static com.jcondotta.banking.accounts.integration.testsupport.rest.RestAssuredTestConstants.API_VERSION_1;
import static com.jcondotta.banking.accounts.integration.testsupport.rest.RestAssuredTestConstants.BASE_URI;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class BankAccountIntegrationSupport {

  @Autowired
  protected BankAccountsURIProperties uriProperties;

  @Autowired
  protected BankAccountRepository bankAccountRepository;

  @Autowired
  private DynamoDbTable<OutboxEntity> outboxTable;

  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = BASE_URI;
    RestAssured.port = port;
  }

  protected BankAccountId openBankAccount(AccountType accountType, Currency currency) {
    var response = postOpenBankAccount(
      BankAccountRequestFactory.openBankAccount(accountType, currency, AccountHolderFixtures.JEFFERSON)
    );

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    return extractBankAccountId(response);
  }

  protected Response postOpenBankAccount(OpenBankAccountRequest request) {
    return given()
      .spec(requestSpecification(uriProperties.rootPath()))
      .body(request)
      .when()
      .post()
      .then()
      .extract()
      .response();
  }

  protected void activateBankAccount(BankAccountId id) {
    patchBankAccount(id, "/activate", HttpStatus.NO_CONTENT);
  }

  protected void blockBankAccount(BankAccountId id) {
    patchBankAccount(id, "/block", HttpStatus.NO_CONTENT);
  }

  protected void unblockBankAccount(BankAccountId id) {
    patchBankAccount(id, "/unblock", HttpStatus.NO_CONTENT);
  }

  protected void closeBankAccount(BankAccountId id) {
    patchBankAccount(id, "/close", HttpStatus.NO_CONTENT);
  }

  protected Response patchBankAccount(BankAccountId id, String actionPath) {
    return given()
      .spec(requestSpecification(uriProperties.bankAccountIdPath() + actionPath))
      .pathParam("bank-account-id", id.value())
      .when()
      .patch()
      .then()
      .extract()
      .response();
  }

  protected void addJointHolder(BankAccountId id, AccountHolderFixtures fixture) {
    var response = postJointHolder(id, BankAccountRequestFactory.addJointHolder(fixture));
    assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  protected Response postJointHolder(BankAccountId id, AddJointHolderRequest request) {
    return given()
      .spec(requestSpecification(uriProperties.bankAccountIdPath() + "/account-holders"))
      .pathParam("bank-account-id", id.value())
      .body(request)
      .when()
      .post()
      .then()
      .extract()
      .response();
  }

  protected BankAccountDetailsResponse getBankAccount(BankAccountId id) {
    return given()
      .spec(requestSpecification(uriProperties.bankAccountIdPath()))
      .pathParam("bank-account-id", id.value())
      .when()
      .get()
      .then()
      .statusCode(HttpStatus.OK.value())
      .extract()
      .as(BankAccountDetailsResponse.class);
  }

  protected Response getBankAccount(UUID id) {
    return given()
      .spec(requestSpecification(uriProperties.bankAccountIdPath()))
      .pathParam("bank-account-id", id)
      .when()
      .get()
      .then()
      .extract()
      .response();
  }

  protected List<OutboxEntity> findOutboxEvents(BankAccountId id) {
    var query = QueryConditional.keyEqualTo(Key.builder()
      .partitionValue("BANK_ACCOUNT#%s".formatted(id.asString()))
      .build());

    return outboxTable.query(query)
      .items()
      .stream()
      .filter(item -> item.getSortKey().startsWith("OUTBOX#"))
      .toList();
  }

  protected void assertOutboxEvents(BankAccountId id, String... eventTypes) {
    var events = findOutboxEvents(id);

    assertThat(events)
      .extracting(OutboxEntity::getEventType)
      .containsExactlyInAnyOrder(eventTypes);

    assertThat(events)
      .allSatisfy(event -> {
        assertThat(event.getAggregateId()).isEqualTo(id.asString());
        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getPayload()).contains(id.asString());
        assertThat(event.getCreatedAt()).isNotNull();
        assertThat(event.getNextAttemptAt()).isNotNull();
      });
  }

  protected RequestSpecification requestSpecification(String basePath) {
    return new RequestSpecBuilder()
      .setBaseUri(RestAssured.baseURI)
      .setPort(RestAssured.port)
      .setBasePath(basePath)
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .addHeader(HttpHeadersConstants.API_VERSION, API_VERSION_1)
      .build();
  }

  private void patchBankAccount(BankAccountId id, String actionPath, HttpStatus expectedStatus) {
    var response = patchBankAccount(id, actionPath);
    assertThat(response.statusCode()).isEqualTo(expectedStatus.value());
  }

  private static BankAccountId extractBankAccountId(Response response) {
    var location = response.getHeader("Location");
    assertThat(location).isNotBlank();

    return BankAccountId.of(UUID.fromString(location.substring(location.lastIndexOf('/') + 1)));
  }
}
