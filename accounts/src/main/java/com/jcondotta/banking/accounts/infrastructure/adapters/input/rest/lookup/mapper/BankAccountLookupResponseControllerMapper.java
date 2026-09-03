package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.AccountStatusResponse;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.AccountTypeResponse;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.BankAccountDetailsResponse;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.CurrencyResponse;
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

    default AccountTypeResponse map(AccountType accountType) {
        return AccountTypeResponse.valueOf(accountType.name());
    }

    default CurrencyResponse map(Currency currency) {
        return CurrencyResponse.valueOf(currency.name());
    }

    default AccountStatusResponse map(AccountStatus accountStatus) {
        return AccountStatusResponse.valueOf(accountStatus.name());
    }
}
