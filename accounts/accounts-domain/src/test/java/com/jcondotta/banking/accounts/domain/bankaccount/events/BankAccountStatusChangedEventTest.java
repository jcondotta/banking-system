package com.jcondotta.banking.accounts.domain.bankaccount.events;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.factory.ClockTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountStatusChangedEventTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.newId();
  private static final AccountStatus PREVIOUS_STATUS = AccountStatus.PENDING;
  private static final AccountStatus CURRENT_STATUS = AccountStatus.ACTIVE;
  private static final Clock FIXED_CLOCK = ClockTestFactory.FIXED_CLOCK;
  private static final Instant OCCURRED_AT = Instant.now(FIXED_CLOCK);

  @Test
  void shouldCreateBankAccountStatusChangedEvent_whenAllArgumentsAreValid() {
    var event = new BankAccountStatusChangedEvent(EVENT_ID, BANK_ACCOUNT_ID, PREVIOUS_STATUS, CURRENT_STATUS, OCCURRED_AT);

    assertThat(event.eventId()).isEqualTo(EVENT_ID);
    assertThat(event.aggregateId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(event.previousStatus()).isEqualTo(PREVIOUS_STATUS);
    assertThat(event.currentStatus()).isEqualTo(CURRENT_STATUS);
    assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
  }

  @Test
  void shouldThrowException_whenEventIdIsNull() {
    assertThatThrownBy(() -> new BankAccountStatusChangedEvent(null, BANK_ACCOUNT_ID, PREVIOUS_STATUS, CURRENT_STATUS, OCCURRED_AT))
      .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new BankAccountStatusChangedEvent(EVENT_ID, null, PREVIOUS_STATUS, CURRENT_STATUS, OCCURRED_AT))
      .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenPreviousStatusIsNull() {
    assertThatThrownBy(() -> new BankAccountStatusChangedEvent(EVENT_ID, BANK_ACCOUNT_ID, null, CURRENT_STATUS, OCCURRED_AT))
      .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenCurrentStatusIsNull() {
    assertThatThrownBy(() -> new BankAccountStatusChangedEvent(EVENT_ID, BANK_ACCOUNT_ID, PREVIOUS_STATUS, null, OCCURRED_AT))
      .isInstanceOf(DomainValidationException.class);
  }

  @Test
  void shouldThrowException_whenOccurredAtIsNull() {
    assertThatThrownBy(() -> new BankAccountStatusChangedEvent(EVENT_ID, BANK_ACCOUNT_ID, PREVIOUS_STATUS, CURRENT_STATUS, null))
      .isInstanceOf(DomainValidationException.class);
  }
}
