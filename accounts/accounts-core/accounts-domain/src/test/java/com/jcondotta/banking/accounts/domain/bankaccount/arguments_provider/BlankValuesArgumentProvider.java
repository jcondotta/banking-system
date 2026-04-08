package com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class BlankValuesArgumentProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return Stream.of(
      Arguments.of(Named.of("Empty String (\"\")", "")),
      Arguments.of(Named.of("Space String (\" \")", " ")),
      Arguments.of(Named.of("Tab String (\"\\t\")", "\t")),
      Arguments.of(Named.of("Line Feed String (\"\\n\")", "\n"))
    );
  }
}