package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.mapper;

import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.model.UpdateRecipientRestRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateRecipientRestMapperTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
  private static final UUID RECIPIENT_ID = UUID.randomUUID();
  private static final String RECIPIENT_NAME = RecipientTestData.JEFFERSON.getName();
  private static final String IBAN = RecipientTestData.JEFFERSON.getIban();

  private final UpdateRecipientRestMapper mapper = new UpdateRecipientRestMapper();

  @Test
  void shouldMapCommand_whenRequestIsProvided() {
    var request = new UpdateRecipientRestRequest(RECIPIENT_NAME, IBAN);

    var command = mapper.toCommand(BANK_ACCOUNT_ID, RECIPIENT_ID, request);

    assertThat(command.bankAccountId().value()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(command.recipientId().value()).isEqualTo(RECIPIENT_ID);
    assertThat(command.recipientName().value()).isEqualTo(RECIPIENT_NAME);
    assertThat(command.iban().value()).isEqualTo(IBAN);
  }
}
