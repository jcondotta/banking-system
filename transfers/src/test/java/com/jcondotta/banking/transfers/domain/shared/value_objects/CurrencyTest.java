package com.jcondotta.banking.transfers.domain.shared.value_objects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyTest {

    @Test
    void shouldAssertCurrencyDetailsCorrectly_whenCurrencyIsEuro() {
        assertThat(Currency.EUR)
            .satisfies(currency -> {
                assertThat(currency.symbol()).isEqualTo(Currency.EUR.symbol());
                assertThat(currency.description()).isEqualTo(Currency.EUR.description());
            });
    }

    @Test
    void shouldAssertCurrencyDetailsCorrectly_whenCurrencyIsUsDollar() {
        assertThat(Currency.USD)
            .satisfies(currency -> {
                assertThat(currency.symbol()).isEqualTo(Currency.USD.symbol());
                assertThat(currency.description()).isEqualTo(Currency.USD.description());
            });
    }
}
