package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.factory.ClockTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountClosedEventTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
  private static final Clock FIXED_CLOCK = ClockTestFactory.FIXED_CLOCK;
  private static final Instant OCCURRED_AT = Instant.now(FIXED_CLOCK);

  @Test
  void shouldCreateBankAccountClosedEvent_whenAllArgumentsAreValid() {
    var event = new BankAccountClosedEvent(EVENT_ID, BANK_ACCOUNT_ID, OCCURRED_AT);

    assertThat(event.eventId()).isEqualTo(EVENT_ID);
    assertThat(event.aggregateId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
  }

  @Test
  void shouldThrowException_whenEventIdIsNull() {
    assertThatThrownBy(() -> new BankAccountClosedEvent(null, BANK_ACCOUNT_ID, OCCURRED_AT))
      .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new BankAccountClosedEvent(EVENT_ID, null, OCCURRED_AT))
      .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenOccurredAtIsNull() {
    assertThatThrownBy(() -> new BankAccountClosedEvent(EVENT_ID, BANK_ACCOUNT_ID, null))
      .isInstanceOf(DomainValidationException.class);
  }
}