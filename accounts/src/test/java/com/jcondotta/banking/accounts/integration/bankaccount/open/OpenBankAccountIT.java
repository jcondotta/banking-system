package com.jcondotta.banking.accounts.integration.bankaccount.open;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.OpenBankAccountCommandHandler;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.testsupport.AccountTypeAndCurrencySource;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.AccountHolderRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.AddressRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.AccountTypeRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.CurrencyRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountIntegrationSupport;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountRequestFactory;
import com.jcondotta.banking.accounts.integration.testsupport.annotation.IntegrationTest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@IntegrationTest
class OpenBankAccountIT extends BankAccountIntegrationSupport {

  private ListAppender<ILoggingEvent> logAppender;

  @BeforeEach
  void attachLogAppender() {
    logAppender = StructuredLogEventSupport.attachAppender(OpenBankAccountCommandHandler.class);
  }

  @AfterEach
  void detachLogAppender() {
    StructuredLogEventSupport.detachAppender(OpenBankAccountCommandHandler.class, logAppender);
  }

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn201CreatedWithValidLocationHeader_whenRequestIsValid(AccountType accountType, Currency currency) {
    var response = postOpenBankAccount(
      BankAccountRequestFactory.openBankAccount(accountType, currency, AccountHolderFixtures.JEFFERSON)
    );

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.getHeader("Content-Type")).isNullOrEmpty();

    var bankAccountId = response.getHeader("Location").substring(response.getHeader("Location").lastIndexOf('/') + 1);
    var bankAccount = bankAccountRepository
      .findById(BankAccountId.of(UUID.fromString(bankAccountId)))
      .orElseThrow();

    assertAll(
      () -> assertThat(bankAccount.getAccountType()).isEqualTo(accountType),
      () -> assertThat(bankAccount.getCurrency()).isEqualTo(currency),
      () -> assertThat(bankAccount.getIban()).isEmpty(),
      () -> assertThat(bankAccount.getAccountStatus()).isEqualTo(BankAccount.ACCOUNT_STATUS_ON_OPENING),
      () -> assertThat(bankAccount.getCreatedAt()).isNotNull(),
      () -> assertThat(bankAccount.getActiveHolders())
        .hasSize(1)
        .singleElement()
        .satisfies(holder -> {
          assertThat(holder.getPersonalInfo()).isEqualTo(AccountHolderFixtures.JEFFERSON.personalInfo());
          assertThat(holder.getContactInfo()).isEqualTo(AccountHolderFixtures.JEFFERSON.contactInfo());
          assertThat(holder.getAddress()).isEqualTo(AccountHolderFixtures.JEFFERSON.address());
          assertThat(holder.isPrimary()).isTrue();
        })
    );

    assertThat(logAppender.list).hasSize(1);
    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    var logAttributes = StructuredLogEventSupport.lastEventKeyValues(logAppender);
    assertThat(logAttributes)
      .containsEntry(LogKey.OPERATION, "accounts.open")
      .containsEntry(LogKey.OUTCOME, "success")
      .containsEntry("bank_account_id", bankAccountId);
    assertThat(logAttributes).containsKey(LogKey.DURATION_MS);
    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getFormattedMessage))
      .isEqualTo("Bank account opened");

    assertOutboxEvents(bankAccount.getId(), BankAccountOpenedEvent.EVENT_TYPE);
  }

  @Test
  void shouldLogSuccessfulReplay_whenSameIdempotencyKeyAndRequestAreReused() {
    var request = BankAccountRequestFactory.openBankAccount(
      AccountType.CHECKING,
      Currency.EUR,
      AccountHolderFixtures.JEFFERSON
    );
    var idempotencyKey = UUID.randomUUID();

    var firstResponse = postOpenBankAccount(request, idempotencyKey);
    var replayResponse = postOpenBankAccount(request, idempotencyKey);

    var softly = new SoftAssertions();
    softly.assertThat(firstResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    softly.assertThat(replayResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    softly.assertThat(replayResponse.getHeader("Location")).isEqualTo(firstResponse.getHeader("Location"));

    assertThat(logAppender.list).hasSize(2);
    var replayAttributes = StructuredLogEventSupport.keyValues(logAppender.list.getLast());
    softly.assertThat(logAppender.list.getLast().getLevel()).isEqualTo(Level.INFO);
    softly.assertThat(replayAttributes)
      .containsEntry(LogKey.OPERATION, "accounts.open")
      .containsEntry(LogKey.OUTCOME, "success")
      .containsEntry(BankAccountLogKey.EXECUTION_RESULT, "replayed")
      .containsEntry("bank_account_id", bankAccountId(firstResponse))
      .containsKey(LogKey.DURATION_MS);
    softly.assertThat(logAppender.list.getLast().getFormattedMessage()).isEqualTo("Bank account opening replayed");
    softly.assertAll();
  }

  @Test
  void shouldLogIdempotencyConflict_whenKeyIsReusedWithDifferentRequestData() {
    var originalRequest = BankAccountRequestFactory.openBankAccount(
      AccountType.CHECKING,
      Currency.EUR,
      AccountHolderFixtures.JEFFERSON
    );
    var conflictingRequest = BankAccountRequestFactory.openBankAccount(
      AccountType.CHECKING,
      Currency.USD,
      AccountHolderFixtures.JEFFERSON
    );
    var idempotencyKey = UUID.randomUUID();

    var firstResponse = postOpenBankAccount(originalRequest, idempotencyKey);
    var conflictingResponse = postOpenBankAccount(conflictingRequest, idempotencyKey);

    var softly = new SoftAssertions();
    softly.assertThat(firstResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    softly.assertThat(conflictingResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());

    assertThat(logAppender.list).hasSize(2);
    var conflictEvent = logAppender.list.getLast();
    var conflictAttributes = StructuredLogEventSupport.keyValues(conflictEvent);
    softly.assertThat(conflictEvent.getLevel()).isEqualTo(Level.ERROR);
    softly.assertThat(conflictAttributes)
      .containsEntry(LogKey.OPERATION, "accounts.open")
      .containsEntry(LogKey.OUTCOME, "failure")
      .containsEntry(LogKey.REASON, "idempotency_conflict")
      .containsKey(LogKey.DURATION_MS);
    softly.assertThat(conflictEvent.getThrowableProxy()).isNotNull();
    softly.assertThat(conflictEvent.getFormattedMessage()).isEqualTo("Idempotency key reused with different request data");
    softly.assertAll();
  }

  private static String bankAccountId(io.restassured.response.Response response) {
    var location = response.getHeader("Location");
    return location.substring(location.lastIndexOf('/') + 1);
  }

  @Test
  void shouldReturn422UnprocessableEntity_whenPrimaryHolderIsMissing() {
    var request = new OpenBankAccountRequest(AccountTypeRequest.CHECKING, CurrencyRequest.EUR, null);

    var response = postOpenBankAccount(request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
  }

  @Test
  void shouldReturn422UnprocessableEntity_whenPersonalInfoIsMissing() {
    var request = new OpenBankAccountRequest(
      AccountTypeRequest.CHECKING,
      CurrencyRequest.EUR,
      new AccountHolderRequest(null, null, null)
    );

    var response = postOpenBankAccount(request);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value());
  }

  @Test
  void shouldDescribeExpectedCountryFormat_whenCountryFormatIsInvalid() {
    var validRequest = BankAccountRequestFactory.openBankAccount(
      AccountType.CHECKING,
      Currency.EUR,
      AccountHolderFixtures.JEFFERSON
    );
    var validHolder = validRequest.primaryHolder();
    var validAddress = validHolder.address();
    var invalidAddress = new AddressRequest(
      validAddress.street(),
      validAddress.streetNumber(),
      validAddress.complement(),
      validAddress.postalCode(),
      validAddress.city(),
      "es"
    );
    var request = new OpenBankAccountRequest(
      validRequest.accountType(),
      validRequest.currency(),
      new AccountHolderRequest(validHolder.personalInfo(), validHolder.contactInfo(), invalidAddress)
    );

    var response = postOpenBankAccount(request);

    assertAll(
      () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value()),
      () -> assertThat(response.jsonPath().getString("errors[0].field"))
        .isEqualTo("primaryHolder.address.country"),
      () -> assertThat(response.jsonPath().getList("errors[0].messages", String.class))
        .containsExactly("must contain exactly two uppercase letters (for example, ES)")
    );
  }
}
