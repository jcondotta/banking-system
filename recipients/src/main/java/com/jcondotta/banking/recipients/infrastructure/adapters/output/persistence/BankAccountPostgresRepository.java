package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.bank_account.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper.BankAccountEntityMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository.BankAccountEntityRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class BankAccountPostgresRepository implements BankAccountRepository {

  private final BankAccountEntityRepository repository;
  private final BankAccountEntityMapper mapper;

  public BankAccountPostgresRepository(
    BankAccountEntityRepository repository,
    BankAccountEntityMapper mapper
  ) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<BankAccount> findById(BankAccountId bankAccountId) {
    return repository.findById(bankAccountId.value()).map(mapper::toDomain);
  }

  @Override
  @Transactional
  public void registerIfAbsent(BankAccount bankAccount) {
    repository.insertIfAbsent(bankAccount.id().value(), bankAccount.status().name());
  }

  @Override
  @Transactional
  public void save(BankAccount bankAccount) {
    repository.upsert(bankAccount.id().value(), bankAccount.status().name());
  }
}
