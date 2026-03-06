package com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class InvalidDniNumberArgumentProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return Stream.of(
      Arguments.of("12345678"),
      Arguments.of("1234567A"),
      Arguments.of("ABCDEFGHZ"),
      Arguments.of("12345678A"),
      Arguments.of("123456789Z")
    );
  }
}