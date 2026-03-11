package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolders;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankAccountEntityKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class BankAccountEntityMapperImpl implements BankAccountEntityMapper {

  private final AccountHolderEntityMapper accountHolderEntityMapper;

  @Override
  public List<BankingEntity> toEntities(BankAccount bankAccount) {
    var bankAccountEntity = toEntity(bankAccount);
    var accountHolderEntities = bankAccount.getActiveHolders().stream()
      .map(accountHolder -> accountHolderEntityMapper.toAccountHolderEntity(bankAccount.getId(), accountHolder))
      .toList();

    return Stream.concat(
      Stream.of(bankAccountEntity),
      accountHolderEntities.stream()
    ).toList();
  }

  BankingEntity toEntity(BankAccount bankAccount) {
    return BankingEntity.builder()
      .partitionKey(BankAccountEntityKey.partitionKey(bankAccount.getId()))
      .sortKey(BankAccountEntityKey.sortKey(bankAccount.getId()))
      .entityType(EntityType.BANK_ACCOUNT)
      .bankAccountId(bankAccount.getId().value())
      .accountType(bankAccount.getAccountType().name())
      .currency(bankAccount.getCurrency().name())
      .iban(bankAccount.getIban().value())
      .status(bankAccount.getAccountStatus().name())
      .createdAt(bankAccount.getCreatedAt())
      .build();
  }

  @Override
  public BankAccount restore(List<BankingEntity> bankingEntities) {
    var bankAccountEntity = findBankAccountEntity(bankingEntities)
      .orElseThrow(() -> new IllegalStateException("Bank account entity not found"));

    var accountHolderEntities = bankingEntities.stream()
      .filter(BankingEntity::isAccountHolder)
      .map(accountHolderEntityMapper::toDomain)
      .toList();

    return BankAccount.restore(
      BankAccountId.of(bankAccountEntity.getBankAccountId()),
      AccountType.valueOf(bankAccountEntity.getAccountType()),
      Currency.valueOf(bankAccountEntity.getCurrency()),
      Iban.of(bankAccountEntity.getIban()),
      AccountStatus.valueOf(bankAccountEntity.getStatus()),
      bankAccountEntity.getCreatedAt(),
      AccountHolders.of(accountHolderEntities)
    );
  }

  private Optional<BankingEntity> findBankAccountEntity(List<BankingEntity> entities) {
    return entities.stream()
      .filter(BankingEntity::isBankAccount)
      .findFirst();
  }
}