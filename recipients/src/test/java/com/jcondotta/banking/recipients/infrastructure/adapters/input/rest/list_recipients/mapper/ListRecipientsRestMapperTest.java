package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.mapper;

import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.model.ListRecipientsRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ListRecipientsRestMapperTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();

  private final ListRecipientsRestMapper mapper = new ListRecipientsRestMapper();

  @Test
  void shouldMapQueryWithoutFilter_whenNameIsNull() {
    var query = mapper.toQuery(BANK_ACCOUNT_ID, new ListRecipientsRequest(1, 10, null));

    assertThat(query.bankAccountId().value()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(query.pageRequest().page()).isEqualTo(1);
    assertThat(query.pageRequest().size()).isEqualTo(10);
    assertThat(query.filter().name()).isEmpty();
  }

  @Test
  void shouldMapQueryWithNameFilter_whenNameIsProvided() {
    var query = mapper.toQuery(BANK_ACCOUNT_ID, new ListRecipientsRequest(1, 10, "jef"));

    assertThat(query.filter().name()).contains("jef");
  }

  @Test
  void shouldTrimNameFilter_whenNameHasSurroundingSpaces() {
    var query = mapper.toQuery(BANK_ACCOUNT_ID, new ListRecipientsRequest(1, 10, "  jef  "));

    assertThat(query.filter().name()).contains("jef");
  }

  @Test
  void shouldMapQueryWithoutFilter_whenNameIsBlank() {
    var query = mapper.toQuery(BANK_ACCOUNT_ID, new ListRecipientsRequest(1, 10, "   "));

    assertThat(query.filter().name()).isEmpty();
  }

  @Test
  void shouldResolveDefaults_whenPageAndSizeAreNull() {
    var query = mapper.toQuery(BANK_ACCOUNT_ID, new ListRecipientsRequest(null, null, null));

    assertThat(query.pageRequest().page()).isEqualTo(ListRecipientsRequest.DEFAULT_PAGE);
    assertThat(query.pageRequest().size()).isEqualTo(ListRecipientsRequest.DEFAULT_SIZE);
    assertThat(query.filter().name()).isEmpty();
  }
}
