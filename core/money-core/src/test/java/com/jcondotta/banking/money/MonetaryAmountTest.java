package com.jcondotta.banking.money;

import com.jcondotta.banking.money.exception.InvalidMonetaryScaleException;
import com.jcondotta.banking.money.exception.NegativeMonetaryAmountException;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MonetaryAmountTest {

    private static final BigDecimal AMOUNT_200 = new BigDecimal("200.00");

    @ParameterizedTest
    @CsvSource({
        "200, EUR",
        "200.0, EUR",
        "200.00, EUR",
        "200, USD",
        "200.0, USD",
        "200.00, USD"
    })
    void shouldCreateCanonicalMonetaryAmount_whenScaleDoesNotExceedCurrencyLimit(
        String amount,
        Currency currency
    ) {
        assertThat(MonetaryAmount.of(new BigDecimal(amount), currency))
            .satisfies(monetaryAmount ->
                assertAll(
                    () -> assertThat(monetaryAmount.amount()).isEqualTo(AMOUNT_200),
                    () -> assertThat(monetaryAmount.currency()).isEqualTo(currency)
                )
            );
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateMonetaryAmount_whenAmountIsZero(Currency currency) {
        assertThat(MonetaryAmount.of(BigDecimal.ZERO, currency))
            .satisfies(monetaryAmount ->
                assertAll(
                    () -> assertThat(monetaryAmount.amount()).isEqualByComparingTo(BigDecimal.ZERO),
                    () -> assertThat(monetaryAmount.currency()).isEqualTo(currency)
                )
            );
    }

    @ParameterizedTest
    @CsvSource({"-0.001", "-0.01", "-1.00", "-100.00"})
    void shouldThrowException_whenAmountIsNegative(String amount) {
        assertThatThrownBy(() -> MonetaryAmount.of(new BigDecimal(amount), Currency.USD))
            .isInstanceOf(NegativeMonetaryAmountException.class)
            .hasMessage(NegativeMonetaryAmountException.AMOUNT_NOT_NEGATIVE_MESSAGE);
    }

    @ParameterizedTest
    @CsvSource({
        "0.000, EUR",
        "10.001, EUR",
        "200.000, EUR",
        "0.000, USD",
        "10.001, USD",
        "200.000, USD"
    })
    void shouldThrowException_whenScaleExceedsCurrencyLimit(String amount, Currency currency) {
        assertThatThrownBy(() -> MonetaryAmount.of(new BigDecimal(amount), currency))
            .isInstanceOf(InvalidMonetaryScaleException.class)
            .hasMessage(
                "Invalid monetary scale for %s: maximum is %d but was 3.",
                currency,
                currency.scale()
            )
            .satisfies(exception -> {
                var invalidScale = (InvalidMonetaryScaleException) exception;
                assertThat(invalidScale.getCurrency()).isEqualTo(currency);
                assertThat(invalidScale.getAllowedScale()).isEqualTo(currency.scale());
                assertThat(invalidScale.getActualScale()).isEqualTo(3);
            });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldThrowException_whenAmountIsNull(Currency currency) {
        assertThatThrownBy(() -> MonetaryAmount.of(null, currency))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(MonetaryAmount.AMOUNT_NOT_PROVIDED);
    }

    @Test
    void shouldThrowException_whenCurrencyIsNull() {
        assertThatThrownBy(() -> MonetaryAmount.of(AMOUNT_200, null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(MonetaryAmount.CURRENCY_NOT_PROVIDED);
    }
}
