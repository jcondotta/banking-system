package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AccountHolderSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
  uses = {
    PersonalInfoSummaryMapper.class,
    ContactInfoSummaryMapper.class,
    AddressSummaryMapper.class
  },
  injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR)
public interface AccountHolderSummaryMapper {
  @Mapping(target = "id", source = "id.value")
  @Mapping(target = "personalInfo", source = "personalInfo")
  @Mapping(target = "contactInfo", source = "contactInfo")
  @Mapping(target = "address", source = "address")
  @Mapping(target = "type", source = "accountHolderType")
  @Mapping(target = "createdAt", source = "createdAt")
  AccountHolderSummary toSummary(AccountHolder accountHolder);
}