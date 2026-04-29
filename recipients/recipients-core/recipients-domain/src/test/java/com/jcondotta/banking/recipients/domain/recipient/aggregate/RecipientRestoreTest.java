package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientRestoreTest {

  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME = RecipientName.of(RecipientTestData.JEFFERSON.getName());
  private static final Iban IBAN = Iban.of(RecipientTestData.JEFFERSON.getIban());
  private static final Instant CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);
  private static final Long VERSION = 1L;

  @Test
  void shouldRestoreRecipient_whenAllValuesAreValid() {
    var recipient = Recipient.restore(
      RECIPIENT_ID,
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN,
      CREATED_AT,
      VERSION
    );

    assertThat(recipient)
      .satisfies(r -> {
        assertThat(r.getId()).isEqualTo(RECIPIENT_ID);
        assertThat(r.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
        assertThat(r.getRecipientName()).isEqualTo(RECIPIENT_NAME);
        assertThat(r.getIban()).isEqualTo(IBAN);
        assertThat(r.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(r.getVersion()).isEqualTo(VERSION);
      });
  }

  @ParameterizedTest
  @CsvSource(
    value = {
      "NULL, false",
      "0, true",
      "1, true"
    },
    nullValues = "NULL"
  )
  void shouldDefineVersionedState_whenRestoringRecipient(Long version, boolean expectedVersioned) {
    var recipient = Recipient.restore(
      RECIPIENT_ID,
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN,
      CREATED_AT,
      version
    );

    assertThat(recipient.getVersion()).isEqualTo(version);
    assertThat(recipient.isVersioned()).isEqualTo(expectedVersioned);
  }

  @Test
  void shouldNotRegisterEvents_whenRestored() {
    var recipient = Recipient.restore(
      RECIPIENT_ID,
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN,
      CREATED_AT,
      VERSION
    );

    assertThat(recipient.hasEvents()).isFalse();
    assertThat(recipient.pullEvents()).isEmpty();
  }

  @Test
  void shouldThrowException_whenRecipientIdIsNullOnRestore() {
    assertThatThrownBy(() -> Recipient.restore(null, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, CREATED_AT, VERSION))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNullOnRestore() {
    assertThatThrownBy(() -> Recipient.restore(RECIPIENT_ID, null, RECIPIENT_NAME, IBAN, CREATED_AT, VERSION))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.BANK_ACCOUNT_ID_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenRecipientNameIsNullOnRestore() {
    assertThatThrownBy(() -> Recipient.restore(RECIPIENT_ID, BANK_ACCOUNT_ID, null, IBAN, CREATED_AT, VERSION))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenIbanIsNullOnRestore() {
    assertThatThrownBy(() -> Recipient.restore(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, null, CREATED_AT, VERSION))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.IBAN_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenCreatedAtIsNullOnRestore() {
    assertThatThrownBy(() -> Recipient.restore(RECIPIENT_ID, BANK_ACCOUNT_ID, RECIPIENT_NAME, IBAN, null, VERSION))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.CREATED_AT_NOT_PROVIDED);
  }
}
