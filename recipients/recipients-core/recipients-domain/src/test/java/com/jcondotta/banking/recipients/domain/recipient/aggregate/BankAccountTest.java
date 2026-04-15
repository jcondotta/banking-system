package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.BankAccountErrors;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

class BankAccountTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME_JEFFERSON =
    RecipientName.of(RecipientTestData.JEFFERSON.getName());
  private static final RecipientName RECIPIENT_NAME_PATRIZIO =
    RecipientName.of(RecipientTestData.PATRIZIO.getName());
  private static final Iban IBAN_JEFFERSON = Iban.of(RecipientTestData.JEFFERSON.getIban());
  private static final Iban IBAN_PATRIZIO = Iban.of(RecipientTestData.PATRIZIO.getIban());
  private static final Instant NOW = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @Test
  void shouldRegisterBankAccountWithActiveStatus_whenBankAccountIdIsValid() {
    var bankAccount = BankAccount.register(BANK_ACCOUNT_ID);

    assertThat(bankAccount.getId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
    assertThat(bankAccount.getActiveRecipients()).isEmpty();
  }

  @Test
  void shouldThrowException_whenRegisterBankAccountWithNullId() {
    assertThatThrownBy(() -> BankAccount.register(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountErrors.ID_MUST_BE_PROVIDED);
  }

  @Test
  void shouldRestoreBankAccount_whenAllValuesAreProvided() {
    var recipients = Recipients.empty();

    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE, recipients);

    assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
  }

  @Test
  void shouldCreateRecipient_whenAccountIsActive() {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE, Recipients.empty());

    var recipient = bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, NOW);

    assertThat(bankAccount.getActiveRecipients()).containsExactly(recipient);
    assertThat(recipient.getCreatedAt()).isEqualTo(NOW);
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, names = "ACTIVE", mode = EXCLUDE)
  void shouldThrowException_whenCreatingRecipientAndAccountIsNotActive(AccountStatus status) {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, status, Recipients.empty());

    assertThatThrownBy(() -> bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, NOW))
      .isInstanceOf(BankAccountNotActiveException.class)
      .hasMessage(BankAccountNotActiveException.BANK_ACCOUNT_MUST_BE_ACTIVE.formatted(status));
  }

  @Test
  void shouldRemoveRecipient_whenAccountIsActive() {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE, Recipients.empty());

    var recipient = bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, NOW);

    bankAccount.removeRecipient(recipient.getId());
    assertThat(bankAccount.getActiveRecipients()).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, names = "ACTIVE", mode = EXCLUDE)
  void shouldThrowException_whenRemovingRecipientAndAccountIsNotActive(AccountStatus status) {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, status, Recipients.empty());

    assertThatThrownBy(() -> bankAccount.removeRecipient(RecipientId.newId()))
        .isInstanceOf(BankAccountNotActiveException.class)
        .hasMessage(BankAccountNotActiveException.BANK_ACCOUNT_MUST_BE_ACTIVE.formatted(status));
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new BankAccount(null, AccountStatus.ACTIVE, Recipients.empty()))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountErrors.ID_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenAccountStatusIsNull() {
    assertThatThrownBy(() -> new BankAccount(BANK_ACCOUNT_ID, null, Recipients.empty()))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountErrors.ACCOUNT_STATUS_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenRecipientsIsNull() {
    assertThatThrownBy(() -> new BankAccount(BANK_ACCOUNT_ID, AccountStatus.ACTIVE, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountErrors.RECIPIENTS_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldReturnOnlyActiveRecipients_whenCallingGetActiveRecipients() {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE, Recipients.empty());
    var recipient1 = bankAccount.createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON, NOW);

    bankAccount.removeRecipient(recipient1.getId());

    Recipient recipient2 = bankAccount.createRecipient(RECIPIENT_NAME_PATRIZIO, IBAN_PATRIZIO, NOW);

    assertThat(bankAccount.getActiveRecipients())
      .containsExactly(recipient2);
  }
}
