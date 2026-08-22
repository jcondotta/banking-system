package com.jcondotta.banking.recipients.application.recipient.query.get;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetRecipientQueryTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  @Test
  void shouldCreateQuery_whenRequiredValuesAreProvided() {
    var query = new GetRecipientQuery(BANK_ACCOUNT_ID, RECIPIENT_ID);

    assertThat(query.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(query.recipientId()).isEqualTo(RECIPIENT_ID);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new GetRecipientQuery(null, RECIPIENT_ID))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(GetRecipientQuery.BANK_ACCOUNT_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenRecipientIdIsNull() {
    assertThatThrownBy(() -> new GetRecipientQuery(BANK_ACCOUNT_ID, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(GetRecipientQuery.RECIPIENT_ID_REQUIRED);
  }
}
