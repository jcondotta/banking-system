package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.list_recipients;

import com.jcondotta.application.core.CommandHandler;
import com.jcondotta.application.core.CommandHandlerWithResult;
import com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient.CreateRecipientCommand;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommand;
import com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient.RemoveRecipientCommand;
import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.properties.RecipientURIProperties;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.testsupport.container.LocalStackTestContainer;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ContextConfiguration(initializers = {LocalStackTestContainer.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ListRecipientsControllerImplIT {

  @Autowired
  private CommandHandler<RegisterBankAccountCommand> registerBankAccountHandler;

  @Autowired
  private CommandHandlerWithResult<CreateRecipientCommand, RecipientId> createRecipientHandler;

  @Autowired
  private CommandHandler<RemoveRecipientCommand> removeRecipientHandler;

  @Autowired
  private RecipientURIProperties uriProperties;

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
  void shouldReturnRecipients_whenBankAccountHasOnlyActiveRecipients() {
    registerBankAccountHandler.handle(
      new RegisterBankAccountCommand(bankAccountId)
    );

    createRecipientHandler.handle(
      new CreateRecipientCommand(
        bankAccountId,
        RecipientFixtures.JEFFERSON.toName(),
        RecipientFixtures.JEFFERSON.toIban()
      )
    );

    createRecipientHandler.handle(
      new CreateRecipientCommand(
        bankAccountId,
        RecipientFixtures.PATRIZIO.toName(),
        RecipientFixtures.PATRIZIO.toIban()
      )
    );

    var response =
      given()
        .spec(requestSpecification)
        .pathParam("bank-account-id", bankAccountId.value())
        .when()
        .get()
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .body()
        .as(ListRecipientsResponse.class);

    assertThat(response.recipients())
      .hasSize(2)
      .extracting(RecipientSummary::recipientName)
      .containsExactlyInAnyOrder(
        RecipientFixtures.JEFFERSON.toName().value(),
        RecipientFixtures.PATRIZIO.toName().value()
      );
  }

  @Test
  void shouldReturnOnlyActiveRecipients_whenBankAccountHasActiveAndInactiveRecipients() {
    registerBankAccountHandler.handle(
      new RegisterBankAccountCommand(bankAccountId)
    );

    createRecipientHandler.handle(new CreateRecipientCommand(
        bankAccountId,
        RecipientFixtures.JEFFERSON.toName(),
        RecipientFixtures.JEFFERSON.toIban()
      )
    );

    var patrizioRecipientId = createRecipientHandler.handle(new CreateRecipientCommand(
          bankAccountId,
          RecipientFixtures.PATRIZIO.toName(),
          RecipientFixtures.PATRIZIO.toIban()
        )
      );

    removeRecipientHandler.handle(new RemoveRecipientCommand(
        bankAccountId,
        patrizioRecipientId
    ));

    var response =
      given()
        .spec(requestSpecification)
        .pathParam("bank-account-id", bankAccountId.value())
      .when()
        .get()
      .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .body()
          .as(ListRecipientsResponse.class);

    assertThat(response.recipients())
      .hasSize(1)
      .extracting(RecipientSummary::recipientName)
      .containsExactly(RecipientFixtures.JEFFERSON.toName().value());
  }

  @Test
  void shouldReturn204NoContent_whenBankAccountHasNoRecipients() {
    registerBankAccountHandler.handle(
      new RegisterBankAccountCommand(bankAccountId)
    );

    given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId.value())
    .when()
      .get()
    .then()
      .statusCode(HttpStatus.NO_CONTENT.value());
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountDoesNotExist() {
    var nonExistentBankAccountId = UUID.randomUUID();

    given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", nonExistentBankAccountId)
    .when()
      .get()
    .then()
      .statusCode(HttpStatus.NOT_FOUND.value());
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
}