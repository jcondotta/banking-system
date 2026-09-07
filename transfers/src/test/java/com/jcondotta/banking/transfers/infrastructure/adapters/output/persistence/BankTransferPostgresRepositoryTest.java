package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.transfers.domain.bank_transfer.aggregate.BankTransfer;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.entity.BankTransferEntity;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.mapper.BankTransferEntityMapper;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.entity.OutboxJpaEntity;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.outbox.entity.OutboxJpaEntityRepository;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.repository.BankTransferEntityRepository;
import com.jcondotta.banking.infrastructure.outbox.collector.OutboxEventCollector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankTransferPostgresRepositoryTest {

  @Mock
  private BankTransferEntityRepository entityRepository;

  @Mock
  private BankTransferEntityMapper mapper;

  @Mock
  private OutboxEventCollector<OutboxJpaEntity> outboxEventCollector;

  @Mock
  private OutboxJpaEntityRepository outboxEntityRepository;

  @InjectMocks
  private BankTransferPostgresRepository repository;

  @Test
  void shouldSaveAndFlushEntityAndOutboxEvents_whenSavingBankTransfer() {
    var bankTransfer = mock(BankTransfer.class);
    var entity = BankTransferEntity.builder()
      .id(UUID.randomUUID())
      .build();
    var outboxEntity = mock(OutboxJpaEntity.class);
    var outboxEntities = List.of(outboxEntity);

    when(mapper.toEntity(bankTransfer)).thenReturn(entity);
    when(outboxEventCollector.collect(bankTransfer)).thenReturn(outboxEntities);

    repository.save(bankTransfer);

    verify(mapper).toEntity(bankTransfer);
    verify(entityRepository).saveAndFlush(entity);
    verify(outboxEventCollector).collect(bankTransfer);
    verify(outboxEntityRepository).saveAllAndFlush(outboxEntities);
  }

  @Test
  void shouldNotSaveOutboxEvents_whenCollectorReturnsEmpty() {
    var bankTransfer = mock(BankTransfer.class);
    var entity = BankTransferEntity.builder()
      .id(UUID.randomUUID())
      .build();

    when(mapper.toEntity(bankTransfer)).thenReturn(entity);
    when(outboxEventCollector.collect(bankTransfer)).thenReturn(List.of());

    repository.save(bankTransfer);

    verify(mapper).toEntity(bankTransfer);
    verify(entityRepository).saveAndFlush(entity);
    verify(outboxEventCollector).collect(bankTransfer);
    verifyNoInteractions(outboxEntityRepository);
  }

  @Test
  void shouldReturnEmpty_whenBankTransferDoesNotExist() {
    var bankTransferId = BankTransferId.newId();

    when(entityRepository.findById(bankTransferId.value())).thenReturn(Optional.empty());

    var result = repository.findById(bankTransferId);

    assertThat(result).isEmpty();
    verify(entityRepository).findById(bankTransferId.value());
    verifyNoInteractions(mapper, outboxEventCollector, outboxEntityRepository);
  }

  @Test
  void shouldReturnBankTransfer_whenEntityExists() {
    var bankTransferId = BankTransferId.newId();
    var entity = BankTransferEntity.builder()
      .id(bankTransferId.value())
      .build();
    var bankTransfer = mock(BankTransfer.class);

    when(entityRepository.findById(bankTransferId.value())).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(bankTransfer);

    var result = repository.findById(bankTransferId);

    assertThat(result).contains(bankTransfer);
    verify(entityRepository).findById(bankTransferId.value());
    verify(mapper).toDomain(entity);
    verifyNoMoreInteractions(outboxEventCollector, outboxEntityRepository);
  }
}
