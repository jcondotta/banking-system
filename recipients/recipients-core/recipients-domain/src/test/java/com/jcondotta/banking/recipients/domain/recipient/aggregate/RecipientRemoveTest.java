package com.jcondotta.banking.recipients.domain.recipient.aggregate;

import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.fixtures.RecipientFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientRemoveTest {

  @Test
  void shouldDeactivateRecipient_whenRemoveIsCalled() {
    var recipient = RecipientFixtures.JEFFERSON.create();

    recipient.remove();

    assertThat(recipient.getStatus()).isEqualTo(RecipientStatus.REMOVED);
  }

  @Test
  void shouldKeepRecipientRemoved_whenRemoveIsCalledTwice() {
    var recipient = RecipientFixtures.JEFFERSON.create();

    recipient.remove();
    assertThat(recipient.getStatus()).isEqualTo(RecipientStatus.REMOVED);

    recipient.remove();
    assertThat(recipient.getStatus()).isEqualTo(RecipientStatus.REMOVED);
  }
}