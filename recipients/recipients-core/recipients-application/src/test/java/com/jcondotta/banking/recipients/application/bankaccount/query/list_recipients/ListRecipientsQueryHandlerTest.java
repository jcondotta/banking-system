package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRecipientsQueryHandlerTest {

  private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

  @Mock
  private RecipientQueryRepository queryRepository;

  @InjectMocks
  private ListRecipientsQueryHandler handler;

  private BankAccountId bankAccountId;

  @BeforeEach
  void setUp() {
    bankAccountId = new BankAccountId(UUID.randomUUID());
  }

  @Test
  void shouldReturnRecipients_whenBankAccountHasActiveRecipients() {
    var recipientsSummary = List.of(
      new RecipientSummary(
        UUID.randomUUID(),
        RecipientFixtures.JEFFERSON.toName().value(),
        RecipientFixtures.JEFFERSON.toIban().value(),
        CREATED_AT
      ),
      new RecipientSummary(
        UUID.randomUUID(),
        RecipientFixtures.PATRIZIO.toName().value(),
        RecipientFixtures.PATRIZIO.toIban().value(),
        CREATED_AT
      )
    );

    when(queryRepository.findActiveByBankAccountId(bankAccountId))
      .thenReturn(recipientsSummary);

    var query = new ListRecipientsQuery(bankAccountId);
    var queryResult = handler.handle(query);

    assertThat(queryResult.recipients()).containsExactlyElementsOf(recipientsSummary);

    verify(queryRepository).findActiveByBankAccountId(bankAccountId);
  }

  @Test
  void shouldReturnEmptyList_whenBankAccountHasNoRecipients() {
    when(queryRepository.findActiveByBankAccountId(bankAccountId))
      .thenReturn(List.of());

    var query = new ListRecipientsQuery(bankAccountId);
    var queryResult = handler.handle(query);

    assertThat(queryResult.recipients()).isEmpty();

    verify(queryRepository).findActiveByBankAccountId(bankAccountId);
  }
}
