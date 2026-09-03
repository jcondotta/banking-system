package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.mapper;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.entity.BankingEntity;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;

import java.util.List;

public interface BankAccountEntityMapper {

  List<BankingEntity> toEntities(BankAccount bankAccount);

  BankAccount restore(List<BankingEntity> bankingEntities);
}