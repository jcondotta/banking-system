package com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidPassportNumberArgumentProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return Stream.of(
      Arguments.of("ABC1234"),
      Arguments.of("ABC123456"),
      Arguments.of("ABC12#34"),
      Arguments.of("ABC1 234"),
      Arguments.of("1234567"),
      Arguments.of("ABCDEFGHI")
    );
  }
}