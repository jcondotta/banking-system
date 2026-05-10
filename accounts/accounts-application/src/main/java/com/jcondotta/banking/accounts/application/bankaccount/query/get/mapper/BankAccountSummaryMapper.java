package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
  componentModel = "spring",
  uses = AccountHolderSummaryMapper.class,
  injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface BankAccountSummaryMapper {

  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "iban", source = "iban.value")
  @Mapping(target = "holders", source = "activeHolders")
  BankAccountSummary toSummary(BankAccount bankAccount);
}