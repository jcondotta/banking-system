package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountActivatedEventTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
  private static final Iban IBAN = BankAccountTestFixture.VALID_IBAN;
  private static final Currency CURRENCY = Currency.EUR;
  private static final Instant OCCURRED_AT = TimeTestFactory.FIXED_INSTANT;

  @Test
  void shouldCreateBankAccountActivatedEvent_whenAllArgumentsAreValid() {
    var event = new BankAccountActivatedEvent(EVENT_ID, BANK_ACCOUNT_ID, IBAN, CURRENCY, OCCURRED_AT);

    assertThat(event.eventId()).isEqualTo(EVENT_ID);
    assertThat(event.aggregateId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(event.iban()).isEqualTo(IBAN.value());
    assertThat(event.currency()).isEqualTo(CURRENCY);
    assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
    assertThat(event.eventType()).isEqualTo(BankAccountActivatedEvent.EVENT_TYPE);
  }

  @Test
  void shouldThrowException_whenEventIdIsNull() {
    assertThatThrownBy(() ->
      new BankAccountActivatedEvent(null, BANK_ACCOUNT_ID, IBAN, CURRENCY, OCCURRED_AT)
    ).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() ->
      new BankAccountActivatedEvent(EVENT_ID, null, IBAN, CURRENCY, OCCURRED_AT)
    ).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenIbanIsNull() {
    assertThatThrownBy(() ->
      new BankAccountActivatedEvent(EVENT_ID, BANK_ACCOUNT_ID, null, CURRENCY, OCCURRED_AT)
    )
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountErrors.IBAN_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenCurrencyIsNull() {
    assertThatThrownBy(() ->
      new BankAccountActivatedEvent(EVENT_ID, BANK_ACCOUNT_ID, IBAN, null, OCCURRED_AT)
    ).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenOccurredAtIsNull() {
    assertThatThrownBy(() ->
      new BankAccountActivatedEvent(EVENT_ID, BANK_ACCOUNT_ID, IBAN, CURRENCY, null)
    ).isInstanceOf(DomainValidationException.class);
  }
}
