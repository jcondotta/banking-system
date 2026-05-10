package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence;

import com.jcondotta.application.query.PageRequest;
import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsFilter;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.RecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper.RecipientSummaryMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository.RecipientEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientQueryPostgresRepositoryTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final PageRequest PAGE_REQUEST = new PageRequest(1, 10);
  private static final ListRecipientsFilter NO_FILTER = ListRecipientsFilter.none();

  @Mock
  private RecipientEntityRepository repository;

  @Mock
  private RecipientSummaryMapper summaryMapper;

  private RecipientQueryPostgresRepository adapter;

  @BeforeEach
  void setUp() {
    adapter = new RecipientQueryPostgresRepository(repository, summaryMapper);
  }

  @Test
  void shouldReturnMappedRecipientSummaries_whenBankAccountHasRecipients() {
    var firstEntity = new RecipientEntity();
    var secondEntity = new RecipientEntity();
    var firstSummary = recipientSummary("Jefferson Condotta");
    var secondSummary = recipientSummary("Patrizio Condotta");

    when(repository.findByBankAccountId(eq(BANK_ACCOUNT_ID.value()), any(Pageable.class)))
      .thenReturn(new PageImpl<>(
        List.of(firstEntity, secondEntity),
        org.springframework.data.domain.PageRequest.of(1, 10, Sort.by("name").ascending()),
        21
      ));
    when(summaryMapper.fromEntity(firstEntity)).thenReturn(firstSummary);
    when(summaryMapper.fromEntity(secondEntity)).thenReturn(secondSummary);

    var result = adapter.findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, NO_FILTER);

    assertThat(result.content()).containsExactly(firstSummary, secondSummary);
    assertThat(result.page()).isEqualTo(1);
    assertThat(result.size()).isEqualTo(10);
    assertThat(result.totalElements()).isEqualTo(21);
    assertThat(result.totalPages()).isEqualTo(3);
    verify(repository).findByBankAccountId(eq(BANK_ACCOUNT_ID.value()), argThat(pageable ->
      pageable.getPageNumber() == 1
        && pageable.getPageSize() == 10
        && pageable.getSort().getOrderFor("name") != null
        && pageable.getSort().getOrderFor("name").isAscending()
    ));
    verify(summaryMapper).fromEntity(firstEntity);
    verify(summaryMapper).fromEntity(secondEntity);
    verifyNoMoreInteractions(repository, summaryMapper);
  }

  @Test
  void shouldReturnEmptyList_whenBankAccountHasNoRecipients() {
    when(repository.findByBankAccountId(eq(BANK_ACCOUNT_ID.value()), any(Pageable.class)))
      .thenReturn(new PageImpl<>(
        List.of(),
        org.springframework.data.domain.PageRequest.of(1, 10, Sort.by("name").ascending()),
        0
      ));

    var result = adapter.findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, NO_FILTER);

    assertThat(result.content()).isEmpty();
    assertThat(result.page()).isEqualTo(1);
    assertThat(result.size()).isEqualTo(10);
    assertThat(result.totalElements()).isZero();
    assertThat(result.totalPages()).isZero();
    verify(repository).findByBankAccountId(eq(BANK_ACCOUNT_ID.value()), argThat(pageable ->
      pageable.getPageNumber() == 1
        && pageable.getPageSize() == 10
        && pageable.getSort().getOrderFor("name") != null
        && pageable.getSort().getOrderFor("name").isAscending()
    ));
    verifyNoInteractions(summaryMapper);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void shouldReturnFilteredRecipientSummaries_whenNameFilterIsProvided() {
    var entity = new RecipientEntity();
    var summary = recipientSummary("Jefferson Condotta");
    var filter = ListRecipientsFilter.byName("jef");

    when(repository.findByBankAccountIdAndNameContainingIgnoreCase(eq(BANK_ACCOUNT_ID.value()), eq("jef"), any(Pageable.class)))
      .thenReturn(new PageImpl<>(
        List.of(entity),
        org.springframework.data.domain.PageRequest.of(1, 10, Sort.by("name").ascending()),
        21
      ));
    when(summaryMapper.fromEntity(entity)).thenReturn(summary);

    var result = adapter.findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, filter);

    assertThat(result.content()).containsExactly(summary);
    assertThat(result.page()).isEqualTo(1);
    assertThat(result.size()).isEqualTo(10);
    assertThat(result.totalElements()).isEqualTo(21);
    assertThat(result.totalPages()).isEqualTo(3);
    verify(repository).findByBankAccountIdAndNameContainingIgnoreCase(eq(BANK_ACCOUNT_ID.value()), eq("jef"), argThat(pageable ->
      pageable.getPageNumber() == 1
        && pageable.getPageSize() == 10
        && pageable.getSort().getOrderFor("name") != null
        && pageable.getSort().getOrderFor("name").isAscending()
    ));
    verify(repository, never()).findByBankAccountId(any(), any());
    verify(summaryMapper).fromEntity(entity);
    verifyNoMoreInteractions(repository, summaryMapper);
  }

  private static RecipientSummary recipientSummary(String name) {
    return new RecipientSummary(
      UUID.randomUUID(),
      name,
      "ES3801283316232166447417",
      Instant.parse("2026-04-17T08:00:00Z")
    );
  }
}
