package com.jcondotta.banking.transfers.domain.bank_account.value_objects;

import com.jcondotta.banking.transfers.domain.testsupport.BlankValuesArgumentProvider;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IbanTest {

    private static final String VALID_SPAIN  = "ES9121000418450200051332";
    private static final String VALID_ITALY  = "IT60X0542811101000000123456";
    private static final String VALID_ITALY_LOWERCASE_WITH_SPACES = "it60 x054 2811 1010 0000 0123 456";

    @Test
    void shouldCreateIban_whenValueIsValid() {
        var iban = Iban.of(VALID_SPAIN);
        assertThat(iban.value()).isEqualTo(VALID_SPAIN);
    }

    @Test
    void shouldCreateSanitizedIban_whenIbanValueIsMessy() {
        var iban = Iban.of(VALID_ITALY_LOWERCASE_WITH_SPACES);
        var expectedSanitized = VALID_ITALY_LOWERCASE_WITH_SPACES.replaceAll("\\s", "").toUpperCase();
        assertThat(iban.value()).isEqualTo(expectedSanitized);
    }

    @Test
    void shouldThrowException_whenIbanValueIsNull() {
        assertThatThrownBy(() -> Iban.of(null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Iban.IBAN_NOT_PROVIDED);
    }

    @ParameterizedTest
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldThrowException_whenIbanValueIsBlank(String blankValue) {
        assertThatThrownBy(() -> Iban.of(blankValue))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Iban.IBAN_NOT_PROVIDED);
    }

    @Test
    void shouldThrowException_whenIbanFormatIsInvalid() {
        assertThatThrownBy(() -> Iban.of("INVALID"))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Iban.IBAN_INVALID_FORMAT);
    }

    @Test
    void shouldThrowException_whenIbanChecksumIsInvalid() {
        assertThatThrownBy(() -> Iban.of("ES9121000418450200051333"))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Iban.IBAN_INVALID_FORMAT);
    }

    @Test
    void shouldBeEqual_whenTwoIbansHaveSameValue() {
        var iban1 = Iban.of(VALID_SPAIN);
        var iban2 = Iban.of(VALID_SPAIN);

        assertThat(iban1).isEqualTo(iban2).hasSameHashCodeAs(iban2);
    }

    @Test
    void shouldNotBeEqual_whenTwoIbansHaveDifferentValues() {
        var iban1 = Iban.of(VALID_SPAIN);
        var iban2 = Iban.of(VALID_ITALY);

        assertThat(iban1).isNotEqualTo(iban2);
    }
}
