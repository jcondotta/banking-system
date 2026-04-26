package com.jcondotta.banking.recipients.domain.testsupport;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;

import java.util.stream.Stream;

public class BlankValuesArgumentProvider implements ArgumentsProvider {

  @Override
  @NullMarked
  public Stream<? extends Arguments> provideArguments(ParameterDeclarations parameters, ExtensionContext context) {
    return Stream.of(
      Arguments.argumentSet("Empty String (\"\")", ""),
      Arguments.argumentSet("Space String (\" \")", " "),
      Arguments.argumentSet("Tab String (\"\\t\")", "\t"),
      Arguments.argumentSet("Line Feed String (\"\\n\")", "\n")
    );
  }
}
