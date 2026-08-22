package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.validation.RecipientError;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RecipientUpdateTest {

  @Test
  void shouldUpdateRecipientNameAndIban() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(of(UUID.randomUUID()));
    var newName = RecipientFixtures.PATRIZIO.toName();
    var newIban = RecipientFixtures.PATRIZIO.toIban();

    recipient.update(newName, newIban);

    assertThat(recipient.getRecipientName()).isEqualTo(newName);
    assertThat(recipient.getIban()).isEqualTo(newIban);
  }

  @Test
  void shouldKeepIdentityBankAccountCreatedAtAndVersion_whenRecipientIsUpdated() {
    var created = RecipientFixtures.JEFFERSON.toRecipient(of(UUID.randomUUID()));
    var recipient = Recipient.restore(
      created.getId(),
      created.getBankAccountId(),
      created.getRecipientName(),
      created.getIban(),
      created.getCreatedAt(),
      1L
    );

    recipient.update(RecipientFixtures.PATRIZIO.toName(), RecipientFixtures.PATRIZIO.toIban());

    assertThat(recipient.getId()).isEqualTo(created.getId());
    assertThat(recipient.getBankAccountId()).isEqualTo(created.getBankAccountId());
    assertThat(recipient.getCreatedAt()).isEqualTo(created.getCreatedAt());
    assertThat(recipient.getVersion()).isEqualTo(1L);
  }

  @Test
  void shouldThrowException_whenRecipientNameIsNull() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(of(UUID.randomUUID()));

    assertThatThrownBy(() -> recipient.update(null, RecipientFixtures.PATRIZIO.toIban()))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenIbanIsNull() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(of(UUID.randomUUID()));

    assertThatThrownBy(() -> recipient.update(RecipientFixtures.PATRIZIO.toName(), null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(RecipientError.IBAN_NOT_PROVIDED);
  }
}
