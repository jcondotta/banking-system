package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.metrics.RecipientMetrics;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.RecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper.RecipientSummaryMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository.RecipientEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientQueryPostgresRepositoryTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Mock
  private RecipientEntityRepository repository;

  @Mock
  private RecipientSummaryMapper summaryMapper;

  @Mock
  private RecipientMetrics recipientMetrics;

  private RecipientQueryPostgresRepository adapter;

  @BeforeEach
  void setUp() {
    adapter = new RecipientQueryPostgresRepository(repository, summaryMapper, recipientMetrics);
  }

  @Test
  void shouldReturnMappedRecipientSummaries_whenBankAccountHasRecipients() {
    var firstEntity = new RecipientEntity();
    var secondEntity = new RecipientEntity();
    var firstSummary = recipientSummary("Jefferson Condotta");
    var secondSummary = recipientSummary("Patrizio Condotta");

    when(repository.findByBankAccountIdOrderByNameAsc(BANK_ACCOUNT_ID.value()))
      .thenReturn(List.of(firstEntity, secondEntity));
    when(summaryMapper.fromEntity(firstEntity)).thenReturn(firstSummary);
    when(summaryMapper.fromEntity(secondEntity)).thenReturn(secondSummary);

    var result = adapter.findByBankAccountId(BANK_ACCOUNT_ID);

    assertThat(result).containsExactly(firstSummary, secondSummary);
    verify(repository).findByBankAccountIdOrderByNameAsc(BANK_ACCOUNT_ID.value());
    verify(summaryMapper).fromEntity(firstEntity);
    verify(summaryMapper).fromEntity(secondEntity);
    verify(recipientMetrics).recordListResultSize(2);
    verifyNoMoreInteractions(repository, summaryMapper, recipientMetrics);
  }

  @Test
  void shouldReturnEmptyList_whenBankAccountHasNoRecipients() {
    when(repository.findByBankAccountIdOrderByNameAsc(BANK_ACCOUNT_ID.value()))
      .thenReturn(List.of());

    assertThat(adapter.findByBankAccountId(BANK_ACCOUNT_ID)).isEmpty();

    verify(repository).findByBankAccountIdOrderByNameAsc(BANK_ACCOUNT_ID.value());
    verify(recipientMetrics).recordListResultSize(0);
    verifyNoInteractions(summaryMapper);
    verifyNoMoreInteractions(repository, recipientMetrics);
  }

  private static RecipientSummary recipientSummary(String name) {
    return new RecipientSummary(
      UUID.randomUUID(),
      BANK_ACCOUNT_ID.value(),
      name,
      "ES3801283316232166447417",
      Instant.parse("2026-04-17T08:00:00Z")
    );
  }
}
