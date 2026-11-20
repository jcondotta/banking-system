package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party;

import com.jcondotta.banking.transfers.domain.testsupport.BlankValuesArgumentProvider;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PartyNameTest {

    private static final String PARTY_NAME_JEFFERSON = "Jefferson Condotta";
    private static final String PARTY_NAME_PATRIZIO = "Patrizio Condotta";

    @Test
    void shouldCreatePartyName_whenValueIsValid() {
        assertThat(PartyName.of(PARTY_NAME_JEFFERSON))
            .isNotNull()
            .extracting(PartyName::value)
            .isEqualTo(PARTY_NAME_JEFFERSON);
    }

    @Test
    void shouldThrowException_whenValueIsNull() {
        assertThatThrownBy(() -> PartyName.of(null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(PartyName.NAME_NOT_PROVIDED);
    }

    @ParameterizedTest
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldThrowException_whenNameIsBlank(String blankValue) {
        assertThatThrownBy(() -> PartyName.of(blankValue))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(PartyName.NAME_NOT_BLANK);
    }

    @Test
    void shouldCreatePartyName_whenNameHasMaxLength() {
        var maxLengthName = "A".repeat(255);

        assertThat(PartyName.of(maxLengthName).value()).isEqualTo(maxLengthName);
    }

    @Test
    void shouldThrowException_whenNameIsTooLong() {
        var longName = "A".repeat(256);

        assertThatThrownBy(() -> PartyName.of(longName))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(PartyName.NAME_TOO_LONG);
    }

    @Test
    void shouldBeEqual_whenNamesHaveSameValue() {
        var partyName1 = PartyName.of(PARTY_NAME_JEFFERSON);
        var partyName2 = PartyName.of(PARTY_NAME_JEFFERSON);

        assertThat(partyName1).isEqualTo(partyName2).hasSameHashCodeAs(partyName2);
    }

    @Test
    void shouldNotBeEqual_whenNamesHaveDifferentValues() {
        var partyName1 = PartyName.of(PARTY_NAME_JEFFERSON);
        var partyName2 = PartyName.of(PARTY_NAME_PATRIZIO);

        assertThat(partyName1).isNotEqualTo(partyName2);
    }
}
