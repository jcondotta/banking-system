package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(
  componentModel = "spring",
  uses = AccountHolderSummaryMapper.class,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BankAccountSummaryMapper {

  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "holders", source = "activeHolders")
  BankAccountSummary toSummary(BankAccount bankAccount);

  default String map(Optional<Iban> iban) {
    return iban.map(Iban::value).orElse(null);
  }
}