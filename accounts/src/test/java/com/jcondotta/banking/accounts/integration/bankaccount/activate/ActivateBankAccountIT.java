package com.jcondotta.banking.accounts.integration.bankaccount.activate;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.domain.testsupport.AccountTypeAndCurrencySource;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountIntegrationSupport;
import com.jcondotta.banking.accounts.integration.testsupport.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@IntegrationTest
class ActivateBankAccountIT extends BankAccountIntegrationSupport {

  private static final String ACTIVATED_TOPIC = "bank-account-activated";

  @Autowired
  private ObjectMapper objectMapper;

  @ParameterizedTest
  @AccountTypeAndCurrencySource
  void shouldReturn204NoContentAndActivateBankAccount_whenBankAccountIsPending(AccountType accountType, Currency currency) {
    var id = openBankAccount(accountType, currency);
    var correlationId = UUID.randomUUID();

    activateBankAccount(id, correlationId);

    var bankAccount = bankAccountRepository.findById(id).orElseThrow();
    var iban = bankAccount.getIban().orElseThrow().value();
    var activatedEvent = findActivatedEvent(id);
    var envelope = objectMapper.readTree(activatedEvent.getPayload());

    assertAll(
      () -> assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE),
      () -> assertThat(activatedEvent.getEventId()).isNotNull(),
      () -> assertThat(activatedEvent.getCorrelationId()).isEqualTo(correlationId),
      () -> assertThat(activatedEvent.getAggregateId()).isEqualTo(id.asString()),
      () -> assertThat(activatedEvent.getMessageKey()).isEqualTo(id.asString()),
      () -> assertThat(activatedEvent.getEventType()).isEqualTo(BankAccountActivatedEvent.EVENT_TYPE),
      () -> assertThat(activatedEvent.getDestination()).isEqualTo(ACTIVATED_TOPIC),
      () -> assertThat(activatedEvent.getAttemptCount()).isZero(),
      () -> assertThat(activatedEvent.getCreatedAt()).isNotNull(),
      () -> assertThat(activatedEvent.getNextAttemptAt()).isNotNull(),
      () -> assertThat(envelope.get("eventId").asText()).isEqualTo(activatedEvent.getEventId().toString()),
      () -> assertThat(envelope.get("correlationId").asText()).isEqualTo(correlationId.toString()),
      () -> assertThat(envelope.get("aggregateId").asText()).isEqualTo(id.asString()),
      () -> assertThat(envelope.get("eventType").asText()).isEqualTo(BankAccountActivatedEvent.EVENT_TYPE),
      () -> assertThat(envelope.get("eventSource").asText()).isEqualTo("accounts"),
      () -> assertThat(envelope.get("occurredAt").asText()).isNotBlank(),
      () -> assertThat(envelope.get("eventVersion").asInt()).isEqualTo(1),
      () -> assertThat(envelope.get("data").get("iban").asText()).isEqualTo(iban),
      () -> assertThat(envelope.get("data").get("currency").asText()).isEqualTo(currency.name())
    );

    assertOutboxEvents(id, BankAccountOpenedEvent.EVENT_TYPE, BankAccountActivatedEvent.EVENT_TYPE);
  }

  @Test
  void shouldReturn404NotFound_whenBankAccountDoesNotExist() {
    var nonExistentId = BankAccountId.of(UUID.fromString("08d8cf86-bc25-4535-8b88-920c07d3e5fe"));

    var response = patchBankAccount(nonExistentId, "/activate");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    assertThat(response.as(ProblemDetail.class).getInstance())
      .isEqualTo(URI.create("/api/bank-accounts/" + nonExistentId.asString() + "/activate"));
  }

  private OutboxEntity findActivatedEvent(BankAccountId id) {
    return findOutboxEvents(id).stream()
      .filter(event -> event.getEventType().equals(BankAccountActivatedEvent.EVENT_TYPE))
      .findFirst()
      .orElseThrow();
  }
}
