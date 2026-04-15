package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientRemoveTest {

  private static final RecipientName RECIPIENT_NAME_JEFFERSON = RecipientName.of(RecipientTestData.JEFFERSON.getName());
  private static final Iban IBAN_JEFFERSON = Iban.of(RecipientTestData.JEFFERSON.getIban());
  private static final Instant CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @Test
  void shouldDeactivateRecipient_whenRemoveIsCalled() {
    var recipient = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);

    assertThat(recipient.getStatus()).isEqualTo(RecipientStatus.ACTIVE);

    recipient.remove();

    assertThat(recipient.getStatus()).isEqualTo(RecipientStatus.REMOVED);
  }

  @Test
  void shouldKeepRecipientRemoved_whenRemoveIsCalledTwice() {
    var recipient = createRecipient(RECIPIENT_NAME_JEFFERSON, IBAN_JEFFERSON);

    assertThat(recipient.getStatus()).isEqualTo(RecipientStatus.ACTIVE);

    recipient.remove();
    assertThat(recipient.getStatus()).isEqualTo(RecipientStatus.REMOVED);

    recipient.remove();
    assertThat(recipient.getStatus()).isEqualTo(RecipientStatus.REMOVED);
  }

  private static Recipient createRecipient(RecipientName recipientName, Iban iban) {
    return Recipient.create(recipientName, iban, CREATED_AT);
  }
}
