package com.jcondotta.banking.transfers.domain.testsupport;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class BlankValuesArgumentProvider implements ArgumentsProvider {

    private static final String DISPLAY_EMPTY_STRING = "\"\"";
    private static final String DISPLAY_BLANK_STRING = "\"   \"";

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
            Arguments.of(Named.of(DISPLAY_EMPTY_STRING, "")),
            Arguments.of(Named.of(DISPLAY_BLANK_STRING, "   "))
        );
    }
}
