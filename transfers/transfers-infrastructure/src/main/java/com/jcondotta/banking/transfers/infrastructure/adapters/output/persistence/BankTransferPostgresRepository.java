package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.transfers.domain.bank_transfer.aggregate.BankTransfer;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.repository.BankTransferRepository;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.mapper.BankTransferEntityMapper;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.repository.BankTransferEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BankTransferPostgresRepository implements BankTransferRepository {

  private final BankTransferEntityRepository repository;
  private final BankTransferEntityMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public Optional<BankTransfer> findById(BankTransferId id) {
    return repository.findById(id.value())
      .map(mapper::toDomain);
  }

  @Override
  @Transactional
  public void save(BankTransfer aggregate) {
    repository.saveAndFlush(mapper.toEntity(aggregate));
  }
}
