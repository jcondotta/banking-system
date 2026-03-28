package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.remove_recipient;

import com.jcondotta.application.core.command.CommandHandler;
import com.jcondotta.application.core.command.CommandHandlerWithResult;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.RecipientFixtures;
import com.jcondotta.banking.infrastructure.web.problem.ProblemTypes;
import com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient.CreateRecipientCommand;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommand;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.properties.AccountRecipientsURIProperties;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.testsupport.container.KafkaTestContainer;
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
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@ContextConfiguration(initializers = { LocalStackTestContainer.class, KafkaTestContainer.class })
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RemoveRecipientControllerImplIT {

  @Autowired
  private CommandHandler<RegisterBankAccountCommand> registerBankAccountHandler;

  @Autowired
  private CommandHandlerWithResult<CreateRecipientCommand, RecipientId> createRecipientHandler;

  @Autowired
  private BankAccountRepository bankAccountRepository;

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
  void shouldReturn204NoContent_whenRecipientIsRemoved() {
    registerBankAccountHandler.handle(
      new RegisterBankAccountCommand(bankAccountId)
    );

    RecipientId recipientId = createRecipientHandler.handle(
      new CreateRecipientCommand(bankAccountId, RecipientFixtures.JEFFERSON.toName(), RecipientFixtures.JEFFERSON.toIban())
    );

    given()
      .spec(requestSpecification)
      .pathParam("bank-account-id", bankAccountId.value())
      .pathParam("recipient-id", recipientId.value())
      .when()
      .delete()
      .then()
      .statusCode(HttpStatus.NO_CONTENT.value());

    var bankAccount = bankAccountRepository.findById(bankAccountId).orElseThrow();

    assertThat(bankAccount.getRecipients())
      .hasSize(1)
      .singleElement()
      .satisfies(recipient -> {
        assertThat(recipient.getStatus()).isEqualTo(RecipientStatus.REMOVED);
        assertThat(recipient.isActive()).isFalse();
      });
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountDoesNotExist() {
    var nonExistentBankAccountId = UUID.randomUUID();
    var recipientId = UUID.randomUUID();

    var problemDetail =
      given()
        .spec(requestSpecification)
        .pathParam("bank-account-id", nonExistentBankAccountId)
        .pathParam("recipient-id", recipientId)
        .when()
        .delete()
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .extract()
        .body()
        .as(ProblemDetail.class);

    var expectedMessage =
      new BankAccountNotFoundException(BankAccountId.of(nonExistentBankAccountId)).getMessage();

    assertAll(
      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.RESOURCE_NOT_FOUND.toString()),
      () -> assertThat(problemDetail.getTitle()).hasToString(HttpStatus.NOT_FOUND.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessage),
      () -> assertThat(problemDetail.getInstance())
        .isEqualTo(uriProperties.recipientURI(nonExistentBankAccountId, recipientId))
    );
  }

  @Test
  void shouldReturn404NotFound_whenRecipientDoesNotExist() {
    registerBankAccountHandler.handle(
      new RegisterBankAccountCommand(bankAccountId)
    );

    var nonExistentRecipientId = UUID.randomUUID();

    var problemDetail =
      given()
        .spec(requestSpecification)
        .pathParam("bank-account-id", bankAccountId.value())
        .pathParam("recipient-id", nonExistentRecipientId)
        .when()
        .delete()
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .extract()
        .body()
        .as(ProblemDetail.class);

    var expectedMessage =
      new RecipientNotFoundException(RecipientId.of(nonExistentRecipientId)).getMessage();

    assertAll(
      () -> assertThat(problemDetail.getType()).hasToString(ProblemTypes.RESOURCE_NOT_FOUND.toString()),
      () -> assertThat(problemDetail.getTitle()).hasToString(HttpStatus.NOT_FOUND.getReasonPhrase()),
      () -> assertThat(problemDetail.getDetail()).isEqualTo(expectedMessage),
      () -> assertThat(problemDetail.getInstance())
        .isEqualTo(uriProperties.recipientURI(bankAccountId.value(), nonExistentRecipientId))
    );
  }

  private RequestSpecification buildRequestSpecification() {
    return new RequestSpecBuilder()
      .setBaseUri(RestAssured.baseURI)
      .setPort(RestAssured.port)
      .setBasePath(uriProperties.recipientIdPath())
      .setContentType(ContentType.JSON)
      .setAccept(ContentType.JSON)
      .build();
  }
}