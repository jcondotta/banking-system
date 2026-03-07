package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.recipient.fixtures.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.validation.BankAccountErrors;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName();
  private static final Iban IBAN = RecipientFixtures.JEFFERSON.toIban();

  @Test
  void shouldRestoreBankAccount_whenAllValuesAreProvided() {
    var recipients = Recipients.empty();

    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE, recipients);

    assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);
    assertThat(bankAccount.getRecipients()).isEmpty();
  }

  @Test
  void shouldCreateRecipient_whenAccountIsActive() {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE, Recipients.empty());

    var recipient = bankAccount.createRecipient(RECIPIENT_NAME, IBAN);

    assertThat(bankAccount.getRecipients())
      .containsExactly(recipient);
  }

  @Test
  void shouldThrowException_whenCreatingRecipientAndAccountIsNotActive() {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.BLOCKED, Recipients.empty());

    assertThatThrownBy(() -> bankAccount.createRecipient(RECIPIENT_NAME, IBAN))
      .isInstanceOf(BankAccountNotActiveException.class)
      .hasMessage(BankAccountNotActiveException.BANK_ACCOUNT_MUST_BE_ACTIVE.formatted(AccountStatus.BLOCKED));
  }

  @Test
  void shouldRemoveRecipient_whenAccountIsActive() {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE, Recipients.empty());

    var recipient = bankAccount.createRecipient(RECIPIENT_NAME, IBAN);

    bankAccount.removeRecipient(recipient.getId());
    assertThat(bankAccount.getRecipients()).isEmpty();
  }

  @Test
  void shouldThrowException_whenRemovingRecipientAndAccountIsNotActive() {
    var recipients = Recipients.empty();
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.BLOCKED, recipients);

    assertThatThrownBy(() -> bankAccount.removeRecipient(RecipientId.newId()))
      .isInstanceOf(BankAccountNotActiveException.class)
      .hasMessage(BankAccountNotActiveException.BANK_ACCOUNT_MUST_BE_ACTIVE.formatted(AccountStatus.BLOCKED));
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
  void shouldReturnOnlyActiveRecipients_whenCallingGetRecipients() {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.ACTIVE, Recipients.empty());
    var recipient1 = bankAccount.createRecipient(RECIPIENT_NAME, IBAN);

    bankAccount.removeRecipient(recipient1.getId());

    var anotherRecipientName = RecipientFixtures.PATRIZIO.toName();
    var anotherIBAN = RecipientFixtures.PATRIZIO.toIban();
    Recipient recipient2 = bankAccount.createRecipient(anotherRecipientName, anotherIBAN);

    assertThat(bankAccount.getRecipients())
      .containsExactly(recipient2);
  }
}