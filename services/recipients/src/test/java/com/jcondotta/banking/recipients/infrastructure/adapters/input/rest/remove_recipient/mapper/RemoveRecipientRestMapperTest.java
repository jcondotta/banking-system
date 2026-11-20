package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.remove_recipient.mapper;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RemoveRecipientRestMapperTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
  private static final UUID RECIPIENT_ID = UUID.randomUUID();

  private final RemoveRecipientRestMapper mapper = new RemoveRecipientRestMapper();

  @Test
  void shouldMapCommand_whenIdsAreProvided() {
    var command = mapper.toCommand(BANK_ACCOUNT_ID, RECIPIENT_ID);

    assertThat(command.bankAccountId().value()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(command.recipientId().value()).isEqualTo(RECIPIENT_ID);
  }
}
