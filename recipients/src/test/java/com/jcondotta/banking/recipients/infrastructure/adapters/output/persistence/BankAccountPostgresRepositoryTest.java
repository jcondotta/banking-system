package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.BankAccountEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper.BankAccountEntityMapper;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository.BankAccountEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountPostgresRepositoryTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Mock
  private BankAccountEntityRepository repository;

  @Mock
  private BankAccountEntityMapper mapper;

  private BankAccountPostgresRepository adapter;

  @BeforeEach
  void setUp() {
    adapter = new BankAccountPostgresRepository(repository, mapper);
  }

  @Test
  void shouldReturnMappedBankAccount_whenAccountExists() {
    var entity = BankAccountEntity.builder().id(BANK_ACCOUNT_ID.value()).status(BankAccountStatus.ACTIVE).build();
    var bankAccount = BankAccount.active(BANK_ACCOUNT_ID);
    when(repository.findById(BANK_ACCOUNT_ID.value())).thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(bankAccount);

    assertThat(adapter.findById(BANK_ACCOUNT_ID)).contains(bankAccount);

    verify(repository).findById(BANK_ACCOUNT_ID.value());
    verify(mapper).toDomain(entity);
  }

  @Test
  void shouldReturnEmpty_whenAccountDoesNotExist() {
    when(repository.findById(BANK_ACCOUNT_ID.value())).thenReturn(Optional.empty());

    assertThat(adapter.findById(BANK_ACCOUNT_ID)).isEmpty();

    verify(repository).findById(BANK_ACCOUNT_ID.value());
    verifyNoInteractions(mapper);
  }

  @Test
  void shouldInsertActivatedAccountOnlyIfAbsent() {
    var bankAccount = BankAccount.active(BANK_ACCOUNT_ID);

    adapter.registerIfAbsent(bankAccount);

    verify(repository).insertIfAbsent(BANK_ACCOUNT_ID.value(), BankAccountStatus.ACTIVE.name());
  }

  @Test
  void shouldUpsertCurrentAccountStatus() {
    var bankAccount = new BankAccount(BANK_ACCOUNT_ID, BankAccountStatus.BLOCKED);

    adapter.save(bankAccount);

    verify(repository).upsert(BANK_ACCOUNT_ID.value(), BankAccountStatus.BLOCKED.name());
  }
}
