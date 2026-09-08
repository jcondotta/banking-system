package com.jcondotta.banking.money;

import com.jcondotta.banking.money.exception.CurrencyMismatchException;
import com.jcondotta.banking.money.exception.InvalidMonetaryScaleException;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class MoneyTest {

    private static final BigDecimal AMOUNT_200 = new BigDecimal("200.00");
    private static final BigDecimal AMOUNT_100 = new BigDecimal("100.00");
    private static final BigDecimal AMOUNT_50 = new BigDecimal("50.00");

    @ParameterizedTest
    @CsvSource({
        "200, EUR",
        "200.0, EUR",
        "200.00, EUR",
        "200, USD",
        "200.0, USD",
        "200.00, USD"
    })
    void shouldCreateCanonicalMoney_whenScaleDoesNotExceedCurrencyLimit(String amount, Currency currency) {
        var money = Money.of(new BigDecimal(amount), currency);

        assertSoftly(softly -> {
            softly.assertThat(money.amount()).isEqualTo(AMOUNT_200);
            softly.assertThat(money.currency()).isEqualTo(currency);
        });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateMoney_whenAmountIsZero(Currency currency) {
        var money = Money.of(BigDecimal.ZERO, currency);

        assertSoftly(softly -> {
            softly.assertThat(money.amount()).isEqualByComparingTo(BigDecimal.ZERO);
            softly.assertThat(money.currency()).isEqualTo(currency);
        });
    }

    @ParameterizedTest
    @CsvSource({"-0.01, EUR", "-1.00, EUR", "-100.00, USD"})
    void shouldCreateMoney_whenAmountIsNegative(String amount, Currency currency) {
        assertThat(Money.of(new BigDecimal(amount), currency).amount())
            .isEqualByComparingTo(new BigDecimal(amount));
    }

    @ParameterizedTest
    @CsvSource({
        "0.000, EUR",
        "10.001, EUR",
        "200.000, USD"
    })
    void shouldThrowException_whenScaleExceedsCurrencyLimit(String amount, Currency currency) {
        assertThatThrownBy(() -> Money.of(new BigDecimal(amount), currency))
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
        assertThatThrownBy(() -> Money.of(null, currency))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Money.AMOUNT_NOT_PROVIDED);
    }

    @Test
    void shouldThrowException_whenCurrencyIsNull() {
        assertThatThrownBy(() -> Money.of(AMOUNT_200, null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Money.CURRENCY_NOT_PROVIDED);
    }

    @Test
    void shouldAddTwoMoneyValues_withSameCurrency() {
        var a = Money.of(AMOUNT_100, Currency.EUR);
        var b = Money.of(AMOUNT_50, Currency.EUR);

        assertThat(a.add(b).amount())
          .isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    void shouldThrowException_whenAddingNullMoney() {
        var eur = Money.of(AMOUNT_100, Currency.EUR);

        assertThatThrownBy(() -> eur.add(null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Money.OTHER_MONEY_NOT_PROVIDED);
    }

    @Test
    void shouldThrowException_whenAddingMoneyWithDifferentCurrency() {
        var eur = Money.of(AMOUNT_100, Currency.EUR);
        var usd = Money.of(AMOUNT_100, Currency.USD);
        assertThatThrownBy(() -> eur.add(usd))
            .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void shouldSubtractTwoMoneyValues_producingNegativeResult() {
        var a = Money.of(AMOUNT_50, Currency.EUR);
        var b = Money.of(AMOUNT_100, Currency.EUR);
        assertThat(a.subtract(b).amount()).isEqualByComparingTo(new BigDecimal("-50.00"));
    }

    @Test
    void shouldThrowException_whenSubtractingNullMoney() {
        var eur = Money.of(AMOUNT_100, Currency.EUR);
        assertThatThrownBy(() -> eur.subtract(null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Money.OTHER_MONEY_NOT_PROVIDED);
    }

    @Test
    void shouldThrowException_whenSubtractingMoneyWithDifferentCurrency() {
        var eur = Money.of(AMOUNT_100, Currency.EUR);
        var usd = Money.of(AMOUNT_50, Currency.USD);
        assertThatThrownBy(() -> eur.subtract(usd))
            .isInstanceOf(CurrencyMismatchException.class);
    }

    @Test
    void shouldReturnTrue_whenAmountIsPositive() {
        assertThat(Money.of(new BigDecimal("1.00"), Currency.EUR).isPositive()).isTrue();
    }

    @Test
    void shouldReturnTrue_whenAmountIsNegative() {
        assertThat(Money.of(new BigDecimal("-1.00"), Currency.EUR).isNegative()).isTrue();
    }

    @Test
    void shouldReturnTrue_whenAmountIsZero() {
        assertThat(Money.of(BigDecimal.ZERO, Currency.EUR).isZero()).isTrue();
    }
}
