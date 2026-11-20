package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountOpenedEventTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
  private static final AccountHolderId ACCOUNT_HOLDER_ID = AccountHolderId.newId();
  private static final AccountType ACCOUNT_TYPE = AccountType.CHECKING;
  private static final Currency CURRENCY = Currency.USD;
  private static final Instant OCCURRED_AT = TimeTestFactory.FIXED_INSTANT;

  @Test
  void shouldCreateBankAccountOpenedEvent_whenAllArgumentsAreValid() {
    var event = new BankAccountOpenedEvent(
      EVENT_ID,
      BANK_ACCOUNT_ID,
      ACCOUNT_TYPE,
      CURRENCY,
      ACCOUNT_HOLDER_ID,
      OCCURRED_AT
    );

    assertThat(event.eventId()).isEqualTo(EVENT_ID);
    assertThat(event.aggregateId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(event.accountType()).isEqualTo(ACCOUNT_TYPE);
    assertThat(event.currency()).isEqualTo(CURRENCY);
    assertThat(event.primaryAccountHolderId()).isEqualTo(ACCOUNT_HOLDER_ID);
    assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
  }

  @Test
  void shouldThrowException_whenEventIdIsNull() {
    assertThatThrownBy(() ->
      new BankAccountOpenedEvent(
        null,
        BANK_ACCOUNT_ID,
        ACCOUNT_TYPE,
        CURRENCY,
        ACCOUNT_HOLDER_ID,
        OCCURRED_AT
      )
    ).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() ->
      new BankAccountOpenedEvent(
        EVENT_ID,
        null,
        ACCOUNT_TYPE,
        CURRENCY,
        ACCOUNT_HOLDER_ID,
        OCCURRED_AT
      )
    ).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenAccountTypeIsNull() {
    assertThatThrownBy(() ->
      new BankAccountOpenedEvent(
        EVENT_ID,
        BANK_ACCOUNT_ID,
        null,
        CURRENCY,
        ACCOUNT_HOLDER_ID,
        OCCURRED_AT
      )
    ).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenCurrencyIsNull() {
    assertThatThrownBy(() ->
      new BankAccountOpenedEvent(
        EVENT_ID,
        BANK_ACCOUNT_ID,
        ACCOUNT_TYPE,
        null,
        ACCOUNT_HOLDER_ID,
        OCCURRED_AT
      )
    ).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenPrimaryAccountHolderIdIsNull() {
    assertThatThrownBy(() ->
      new BankAccountOpenedEvent(
        EVENT_ID,
        BANK_ACCOUNT_ID,
        ACCOUNT_TYPE,
        CURRENCY,
        null,
        OCCURRED_AT
      )
    ).isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenOccurredAtIsNull() {
    assertThatThrownBy(() ->
      new BankAccountOpenedEvent(
        EVENT_ID,
        BANK_ACCOUNT_ID,
        ACCOUNT_TYPE,
        CURRENCY,
        ACCOUNT_HOLDER_ID,
        null
      )
    ).isInstanceOf(DomainValidationException.class);
  }
}
