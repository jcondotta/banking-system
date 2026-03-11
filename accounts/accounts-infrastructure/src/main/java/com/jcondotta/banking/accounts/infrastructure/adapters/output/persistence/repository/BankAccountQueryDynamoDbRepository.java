package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.BankAccountQueryRepository;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper.BankAccountSummaryMapper;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@AllArgsConstructor
public class BankAccountQueryDynamoDbRepository implements BankAccountQueryRepository {

  private final BankAccountRepository bankAccountRepository;
  private final BankAccountSummaryMapper bankAccountSummaryMapper;

  @Override
  public Optional<BankAccountSummary> findById(BankAccountId bankAccountId) {
    return bankAccountRepository.findById(bankAccountId)
      .map(bankAccountSummaryMapper::toSummary);
  }
}