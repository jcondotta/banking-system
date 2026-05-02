package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.recipients.application.common.exception.RecipientOptimisticLockException;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.RecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper.RecipientEntityMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository.RecipientEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipientPostgresRepositoryTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Mock
  private RecipientEntityRepository repository;

  @Mock
  private RecipientEntityMapper mapper;

  private RecipientPostgresRepository adapter;

  @BeforeEach
  void setUp() {
    adapter = new RecipientPostgresRepository(repository, mapper);
  }

  @Test
  void shouldReturnMappedRecipient_whenRecipientExists() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(BANK_ACCOUNT_ID);
    var entity = new RecipientEntity();

    when(repository.findById(recipient.getId().value())).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(recipient);

    var result = adapter.findById(recipient.getId());

    assertThat(result).contains(recipient);
    verify(repository).findById(recipient.getId().value());
    verify(mapper).toDomain(entity);
    verifyNoMoreInteractions(repository, mapper);
  }

  @Test
  void shouldReturnEmpty_whenRecipientDoesNotExist() {
    var recipientId = RecipientId.newId();

    when(repository.findById(recipientId.value())).thenReturn(Optional.empty());

    assertThat(adapter.findById(recipientId)).isEmpty();

    verify(repository).findById(recipientId.value());
    verifyNoInteractions(mapper);
  }

  @Test
  void shouldSaveRecipient_whenRecipientIsCreated() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(BANK_ACCOUNT_ID);
    var entity = new RecipientEntity();

    when(mapper.toEntity(recipient)).thenReturn(entity);

    adapter.save(recipient);

    verify(mapper).toEntity(recipient);
    verify(repository).saveAndFlush(entity);
    verifyNoMoreInteractions(repository, mapper);
  }

  @Test
  void shouldThrowDuplicateRecipientIbanException_whenUniqueIbanConstraintIsViolated() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(BANK_ACCOUNT_ID);
    var entity = new RecipientEntity();

    when(mapper.toEntity(recipient)).thenReturn(entity);
    when(repository.saveAndFlush(entity))
      .thenThrow(dataIntegrityViolation("uq_recipient_bank_account_iban"));

    assertThatThrownBy(() -> adapter.save(recipient))
      .isInstanceOf(DuplicateRecipientIbanException.class);

    verify(mapper).toEntity(recipient);
    verify(repository).saveAndFlush(entity);
    verifyNoMoreInteractions(repository, mapper);
  }

  @Test
  void shouldDeleteRecipient_whenVersionMatches() {
    var recipient = recipientWithVersion();

    when(repository.deleteIfVersionMatches(recipient.getId().value(), recipient.getVersion()))
      .thenReturn(1);

    adapter.delete(recipient);

    verify(repository).deleteIfVersionMatches(recipient.getId().value(), recipient.getVersion());
    verifyNoMoreInteractions(repository);
    verifyNoInteractions(mapper);
  }

  @Test
  void shouldTreatDeleteAsIdempotent_whenVersionDoesNotMatchAndRecipientDoesNotExist() {
    var recipient = recipientWithVersion();

    when(repository.deleteIfVersionMatches(recipient.getId().value(), recipient.getVersion()))
      .thenReturn(0);
    when(repository.existsById(recipient.getId().value())).thenReturn(false);

    adapter.delete(recipient);

    verify(repository).deleteIfVersionMatches(recipient.getId().value(), recipient.getVersion());
    verify(repository).existsById(recipient.getId().value());
    verifyNoMoreInteractions(repository);
    verifyNoInteractions(mapper);
  }

  @Test
  void shouldThrowOptimisticLockException_whenRecipientStillExistsWithDifferentVersion() {
    var recipient = recipientWithVersion();

    when(repository.deleteIfVersionMatches(recipient.getId().value(), recipient.getVersion()))
      .thenReturn(0);
    when(repository.existsById(recipient.getId().value())).thenReturn(true);

    assertThatThrownBy(() -> adapter.delete(recipient))
      .isInstanceOf(RecipientOptimisticLockException.class);

    verify(repository).deleteIfVersionMatches(recipient.getId().value(), recipient.getVersion());
    verify(repository).existsById(recipient.getId().value());
    verifyNoMoreInteractions(repository);
    verifyNoInteractions(mapper);
  }

  private static Recipient recipientWithVersion() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient(BANK_ACCOUNT_ID);

    return Recipient.restore(
      recipient.getId(),
      recipient.getBankAccountId(),
      recipient.getRecipientName(),
      recipient.getIban(),
      recipient.getCreatedAt(),
      0L
    );
  }

  private static DataIntegrityViolationException dataIntegrityViolation(String message) {
    return new DataIntegrityViolationException("constraint violation", new RuntimeException(message));
  }
}
