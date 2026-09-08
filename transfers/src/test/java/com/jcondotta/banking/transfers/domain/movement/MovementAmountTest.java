package com.jcondotta.banking.transfers.domain.movement;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.money.Money;
import com.jcondotta.banking.money.exception.InvalidMonetaryScaleException;
import com.jcondotta.banking.transfers.domain.movement.exception.NegativeMovementAmountException;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class MovementAmountTest {

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
    void shouldCreateCanonicalMovementAmount_whenScaleDoesNotExceedCurrencyLimit(String amount, Currency currency) {
        var movementAmount = MovementAmount.of(new BigDecimal(amount), currency);

        assertSoftly(softly -> {
            softly.assertThat(movementAmount.amount()).isEqualTo(AMOUNT_200);
            softly.assertThat(movementAmount.currency()).isEqualTo(currency);
        });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateMovementAmount_whenAmountIsZero(Currency currency) {
        var movementAmount = MovementAmount.of(BigDecimal.ZERO, currency);

        assertSoftly(softly -> {
            softly.assertThat(movementAmount.amount()).isEqualByComparingTo(BigDecimal.ZERO);
            softly.assertThat(movementAmount.currency()).isEqualTo(currency);
        });
    }

    @ParameterizedTest
    @CsvSource({"-0.01", "-1.00", "-100.00"})
    void shouldThrowException_whenAmountIsNegative(String amount) {
        assertThatThrownBy(() -> MovementAmount.of(new BigDecimal(amount), Currency.USD))
            .isInstanceOf(NegativeMovementAmountException.class)
            .hasMessage(NegativeMovementAmountException.AMOUNT_NOT_NEGATIVE_MESSAGE);
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
        assertThatThrownBy(() -> MovementAmount.of(new BigDecimal(amount), currency))
            .isInstanceOf(InvalidMonetaryScaleException.class)
            .hasMessage(
                "Invalid monetary scale for %s: maximum is %d but was 3.",
                currency,
                currency.scale()
            );
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldThrowException_whenAmountIsNull(Currency currency) {
        assertThatThrownBy(() -> MovementAmount.of(null, currency))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Money.AMOUNT_NOT_PROVIDED);
    }

    @Test
    void shouldThrowException_whenCurrencyIsNull() {
        assertThatThrownBy(() -> MovementAmount.of(AMOUNT_200, null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Money.CURRENCY_NOT_PROVIDED);
    }
}
