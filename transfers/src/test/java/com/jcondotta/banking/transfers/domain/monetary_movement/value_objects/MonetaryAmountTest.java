package com.jcondotta.banking.transfers.domain.monetary_movement.value_objects;

import com.jcondotta.banking.transfers.domain.monetary_movement.exceptions.NegativeMonetaryAmountException;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;
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
    @EnumSource(Currency.class)
    void shouldCreateMonetaryAmount_whenParametersAreValid(Currency currency) {
        assertThat(MonetaryAmount.of(AMOUNT_200, currency))
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
    @CsvSource({"-0.01", "-1.00", "-100.00"})
    void shouldThrowException_whenAmountIsNegative(String amount) {
        assertThatThrownBy(() -> MonetaryAmount.of(new BigDecimal(amount), Currency.USD))
            .isInstanceOf(NegativeMonetaryAmountException.class)
            .hasMessage(NegativeMonetaryAmountException.AMOUNT_NOT_NEGATIVE_MESSAGE);
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
