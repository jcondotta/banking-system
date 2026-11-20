package com.jcondotta.banking.money;

import com.jcondotta.banking.money.exception.CurrencyMismatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrencyTest {

    @Test
    void shouldAssertCurrencyDetailsCorrectly_whenCurrencyIsEuro() {
        assertThat(Currency.EUR)
            .satisfies(currency -> {
                assertThat(currency.symbol()).isEqualTo("€");
                assertThat(currency.description()).isEqualTo("Euro");
                assertThat(currency.scale()).isEqualTo(2);
            });
    }

    @Test
    void shouldAssertCurrencyDetailsCorrectly_whenCurrencyIsUsDollar() {
        assertThat(Currency.USD)
            .satisfies(currency -> {
                assertThat(currency.symbol()).isEqualTo("$");
                assertThat(currency.description()).isEqualTo("US Dollar");
                assertThat(currency.scale()).isEqualTo(2);
            });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldNotThrowException_whenCurrencyIsTheSame(Currency currency) {
        assertThatCode(() -> currency.requireSameAs(currency))
            .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @CsvSource({"EUR, USD", "USD, EUR"})
    void shouldThrowException_whenCurrencyIsDifferent(Currency expected, Currency actual) {
        assertThatThrownBy(() -> expected.requireSameAs(actual))
            .isInstanceOf(CurrencyMismatchException.class)
            .hasMessage("Currency mismatch: expected %s but was %s.", expected, actual)
            .satisfies(exception -> {
                var mismatch = (CurrencyMismatchException) exception;
                assertThat(mismatch.getExpected()).isEqualTo(expected);
                assertThat(mismatch.getActual()).isEqualTo(actual);
            });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldThrowException_whenActualCurrencyIsNull(Currency expected) {
        assertThatThrownBy(() -> expected.requireSameAs(null))
            .isInstanceOf(CurrencyMismatchException.class)
            .hasMessage("Currency mismatch: expected %s but was null.", expected)
            .satisfies(exception -> {
                var mismatch = (CurrencyMismatchException) exception;
                assertThat(mismatch.getExpected()).isEqualTo(expected);
                assertThat(mismatch.getActual()).isNull();
            });
    }
}
