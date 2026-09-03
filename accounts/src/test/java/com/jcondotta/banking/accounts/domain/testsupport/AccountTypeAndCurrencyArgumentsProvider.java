package com.jcondotta.banking.accounts.domain.testsupport;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.stream.Stream;

public class AccountTypeAndCurrencyArgumentsProvider implements ArgumentsProvider {

  @Override
  @NullMarked
  public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters, ExtensionContext context) {
    return Stream.of(
      Arguments.argumentSet("Checking EUR", AccountType.CHECKING, Currency.EUR),
      Arguments.argumentSet("Checking USD", AccountType.CHECKING, Currency.USD),
      Arguments.argumentSet("Savings EUR", AccountType.SAVINGS, Currency.EUR),
      Arguments.argumentSet("Savings USD", AccountType.SAVINGS, Currency.USD)
    );
  }
}
