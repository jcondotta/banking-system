package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount.BankAccountDetailsResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR,
  uses = AccountHolderDetailsResponseMapper.class
)
public interface BankAccountLookupResponseControllerMapper {

    BankAccountDetailsResponse toBankAccountDetailsResponse(BankAccountSummary bankAccountSummary);
}
