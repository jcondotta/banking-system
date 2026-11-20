package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.get_recipient.mapper;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GetRecipientRestMapperTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
  private static final UUID RECIPIENT_ID = UUID.randomUUID();

  private final GetRecipientRestMapper mapper = new GetRecipientRestMapper();

  @Test
  void shouldMapQuery_whenIdentifiersAreProvided() {
    var query = mapper.toQuery(BANK_ACCOUNT_ID, RECIPIENT_ID);

    assertThat(query.bankAccountId().value()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(query.recipientId().value()).isEqualTo(RECIPIENT_ID);
  }
}
