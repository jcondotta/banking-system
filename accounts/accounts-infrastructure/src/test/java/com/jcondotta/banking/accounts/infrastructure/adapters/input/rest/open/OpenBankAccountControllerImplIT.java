package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcondotta.bankaccounts.contracts.DefaultIntegrationEventMetadata;
import com.jcondotta.bankaccounts.contracts.open.BankAccountOpenedIntegrationPayload;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.repository.BankAccountDynamoDbRepository;
import com.jcondotta.banking.accounts.infrastructure.arguments_provider.AccountTypeAndCurrencyArgumentsProvider;
import com.jcondotta.banking.accounts.infrastructure.container.KafkaTestContainer;
import com.jcondotta.banking.accounts.infrastructure.container.LocalStackTestContainer;
import com.jcondotta.banking.accounts.infrastructure.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.infrastructure.properties.BankAccountsURIProperties;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ContextConfiguration(initializers = { LocalStackTestContainer.class, KafkaTestContainer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenBankAccountControllerImplIT {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;

  private static final Currency CURRENCY_EUR = Currency.EUR;
  private static final AccountType ACCOUNT_TYPE_CHECKING = AccountType.CHECKING;
//
//  @Autowired
//  Clock testClockUTC;
//
  @Autowired
  ObjectMapper objectMapper;
//
  @Autowired
  BankAccountsURIProperties uriProperties;

  @Autowired
  private BankAccountRepository bankAccountRepository;

  RequestSpecification requestSpecification;
//
  @BeforeAll
  static void beforeAll() {
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  @BeforeEach
  void beforeEach(@LocalServerPort int port) {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;

    requestSpecification = new RequestSpecBuilder()
      .setBaseUri(RestAssured.baseURI)
      .setPort(RestAssured.port)
      .setBasePath(uriProperties.rootPath())
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .build();
  }

  @ParameterizedTest
  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
  void shouldReturn201CreatedWithValidLocationHeader_whenRequestIsValid(AccountType accountType, Currency currency) {
    var openBankAccountRequest = getOpenBankAccountRequest(accountType, currency);

    var response = given()
      .spec(requestSpecification)
      .body(openBankAccountRequest)
    .when()
      .post()
    .then()
      .statusCode(HttpStatus.CREATED.value())
      .extract()
      .response();

    var location = response.header("location");
    assertThat(location).isNotBlank();

    UUID bankAccountId = UUID.fromString(
      location.substring(location.lastIndexOf('/') + 1)
    );

    var bankAccount = bankAccountRepository
      .findById(BankAccountId.of(bankAccountId))
      .orElseThrow(() -> new RuntimeException("Bank account not found"));

    assertThat(bankAccount.getId().value()).isEqualTo(bankAccountId);
    assertThat(bankAccount.getAccountType()).isEqualTo(accountType);
    assertThat(bankAccount.getCurrency()).isEqualTo(currency);
    assertThat(bankAccount.getIban()).isNotNull();
    assertThat(bankAccount.getAccountStatus()).isEqualTo(BankAccount.ACCOUNT_STATUS_ON_OPENING);
    assertThat(bankAccount.getCreatedAt()).isNotNull();
    assertThat(bankAccount.getActiveHolders())
      .hasSize(1)
      .singleElement()
      .satisfies(accountHolder -> {
        assertThat(accountHolder.getId()).isNotNull();
        assertThat(accountHolder.name()).isEqualTo(PRIMARY_ACCOUNT_HOLDER.getAccountHolderName());
        assertThat(accountHolder.identityDocument()).isEqualTo(PRIMARY_ACCOUNT_HOLDER.getIdentityDocument());
        assertThat(accountHolder.dateOfBirth()).isEqualTo(PRIMARY_ACCOUNT_HOLDER.getDateOfBirth());
        assertThat(accountHolder.getContactInfo()).isEqualTo(PRIMARY_ACCOUNT_HOLDER.getEmail());
        assertThat(accountHolder.isPrimary()).isTrue();
        assertThat(accountHolder.getCreatedAt()).isNotNull();
      });

    var impl = (BankAccountDynamoDbRepository) bankAccountRepository;
    var outboxEvents = impl.findOutboxEvents(bankAccountId);

    System.out.println(outboxEvents);

    assertThat(outboxEvents)
      .hasSize(1)
      .singleElement()
      .satisfies(outboxEvent -> {
//        OutboxKey key = OutboxKeyFactory.pending(
//          id,
//          integrationEvent.metadata().eventId(),
//          integrationEvent.metadata().occurredAt()
//        );


//        assertThat(outboxEvent.getPartitionKey()).isEqualTo(OUTBO);
        assertThat(outboxEvent.getAggregateId()).isEqualTo(bankAccountId);
        assertThat(outboxEvent.getEventId()).isNotNull();
        assertThat(outboxEvent.getEventType()).isEqualTo("bank-account-opened"); // algo q nao seja de dominio
        assertThat(outboxEvent.getEntityType()).isEqualTo(EntityType.OUTBOX_EVENT);

        assertThat(outboxEvent.getPublishedAt()).isNotNull();


//        private String partitionKey;
//        private String sortKey;
//
//        private String gsi1pk;
//        private String gsi1sk;
//
//        private EntityType entityType;
//
//        private UUID eventId;
//        private UUID aggregateId;
//        private String eventType;
//
//        private String payload;
//        private Instant publishedAt;



        var root = objectMapper.readTree(outboxEvent.getPayload());
        var eventMetadata = objectMapper.treeToValue(
          root.get("metadata"),
          DefaultIntegrationEventMetadata.class
        );

        assertThat(eventMetadata.eventId()).isNotNull();
        assertThat(eventMetadata.eventType()).isNotNull();

        var payload = objectMapper.treeToValue(
          root.get("payload"),
          BankAccountOpenedIntegrationPayload.class
        );

        assertThat(payload.bankAccountId()).isEqualTo(bankAccountId);
        assertThat(payload.accountType()).isEqualTo(accountType.toString());
        assertThat(payload.currency()).isEqualTo(currency.toString());
        assertThat(payload.accountHolderId())
          .isEqualTo(bankAccount.getPrimaryHolder().getId().value());
      });
  }

  private static @NotNull OpenBankAccountRequest getOpenBankAccountRequest(AccountType accountType, Currency currency) {
    var primaryAccountHolderRequest = new PrimaryAccountHolderRequest(
      PRIMARY_ACCOUNT_HOLDER.getAccountHolderName().value(),
      new IdentityDocumentRequest(
        PRIMARY_ACCOUNT_HOLDER.getIdentityDocument().type().name(),
        PRIMARY_ACCOUNT_HOLDER.getIdentityDocument().number().value()
      ),
      PRIMARY_ACCOUNT_HOLDER.getDateOfBirth().value(),
      PRIMARY_ACCOUNT_HOLDER.getEmail().value()
    );
    var request = new OpenBankAccountRequest(accountType, currency, primaryAccountHolderRequest);
    return request;
  }

//  @ParameterizedTest
//  @ArgumentsSource(BlankValuesArgumentProvider.class)
//  void shouldReturn422WithValidationProblemDetails_whenAccountHolderNameIsBlank(String blankName) throws JsonProcessingException {
//
//    var primaryAccountHolderRequest =
//      new PrimaryAccountHolderRequest(blankName, VALID_PASSPORT, VALID_DATE_OF_BIRTH, VALID_EMAIL);
//
//    var request =
//      new OpenBankAccountRequest(AccountType.CHECKING, Currency.EUR, primaryAccountHolderRequest);
//
//    given()
//      .spec(requestSpecification)
//      .body(request)
//    .when()
//      .post()
//    .then()
//      .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
//      .body("instance", equalTo(uriProperties.rootPath()))
//      .body("properties.errors", hasSize(1))
//      .body("properties.errors[0].field", equalTo("accountHolder.holderName"))
//      .body("properties.errors[0].messages[0]", equalTo("must not be blank"));
//  }
}