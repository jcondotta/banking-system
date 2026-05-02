package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.mapper;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListRecipientsRestMapperTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();

  private final ListRecipientsRestMapper mapper = new ListRecipientsRestMapper();

  @Test
  void shouldMapQuery_whenBankAccountIdIsProvided() {
    var query = mapper.toQuery(BANK_ACCOUNT_ID, 1, 10);

    assertThat(query.bankAccountId().value()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(query.pageRequest().page()).isEqualTo(1);
    assertThat(query.pageRequest().size()).isEqualTo(10);
  }
}
