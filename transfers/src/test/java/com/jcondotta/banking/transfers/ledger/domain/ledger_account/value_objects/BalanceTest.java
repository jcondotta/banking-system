package com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.money.MonetaryAmount;
import com.jcondotta.banking.money.MonetaryMovement;
import com.jcondotta.banking.money.exception.CurrencyMismatchException;
import com.jcondotta.domain.exception.DomainValidationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BalanceTest {

    private static final BigDecimal AMOUNT_100 = new BigDecimal("100.00");
    private static final BigDecimal AMOUNT_50 = new BigDecimal("50.00");
    private static final BigDecimal AMOUNT_30 = new BigDecimal("30.00");

    private static Currency otherCurrencyThan(Currency currency) {
        return Arrays.stream(Currency.values())
            .filter(c -> !c.equals(currency))
            .findFirst()
            .orElseThrow();
    }

    @Nested
    class WhenCreatingBalance {

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldCreateBalance_whenAmountAndCurrencyAreValid(Currency currency) {
            var balance = new Balance(AMOUNT_100, currency);

            assertThat(balance.amount()).isEqualByComparingTo(AMOUNT_100);
            assertThat(balance.currency()).isEqualTo(currency);
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldScaleAmountToTwoDecimalPlaces_whenCreated(Currency currency) {
            var balance = new Balance(new BigDecimal("100"), currency);

            assertThat(balance.amount().scale()).isEqualTo(2);
            assertThat(balance.amount()).isEqualByComparingTo(AMOUNT_100);
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldAllowNegativeAmount_whenCreated(Currency currency) {
            var negativeAmount = new BigDecimal("-50.00");
            var balance = new Balance(negativeAmount, currency);

            assertThat(balance.amount()).isEqualByComparingTo(negativeAmount);
            assertThat(balance.isNegative()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldThrowException_whenAmountIsNull(Currency currency) {
            assertThatThrownBy(() -> new Balance(null, currency))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage(Balance.AMOUNT_NOT_PROVIDED);
        }

        @Test
        void shouldThrowException_whenCurrencyIsNull() {
            assertThatThrownBy(() -> new Balance(AMOUNT_100, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage(Balance.CURRENCY_NOT_PROVIDED);
        }
    }

    @Nested
    class WhenCreatingZeroBalance {

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldCreateBalanceWithZeroAmount_whenCurrencyIsValid(Currency currency) {
            var balance = Balance.zero(currency);

            assertThat(balance.amount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(balance.currency()).isEqualTo(currency);
            assertThat(balance.isNegative()).isFalse();
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldNormalizeScaleToTwo_whenCreatedWithBigDecimalZero(Currency currency) {
            var balance = Balance.zero(currency);

            assertThat(balance.amount().scale()).isEqualTo(2);
        }

        @Test
        void shouldThrowException_whenCurrencyIsNull() {
            assertThatThrownBy(() -> Balance.zero(null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage(Balance.CURRENCY_NOT_PROVIDED);
        }
    }

    @Nested
    class WhenApplyingMonetaryMovement {

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldDecreaseBalance_whenMovementIsDebit(Currency currency) {
            var balance = new Balance(AMOUNT_100, currency);
            var debitMovement = MonetaryMovement.ofDebit(MonetaryAmount.of(AMOUNT_30, currency));

            var result = balance.apply(debitMovement);

            assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("70.00"));
            assertThat(result.currency()).isEqualTo(currency);
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldIncreaseBalance_whenMovementIsCredit(Currency currency) {
            var balance = new Balance(AMOUNT_100, currency);
            var creditMovement = MonetaryMovement.ofCredit(MonetaryAmount.of(AMOUNT_50, currency));

            var result = balance.apply(creditMovement);

            assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("150.00"));
            assertThat(result.currency()).isEqualTo(currency);
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldAllowBalanceToGoBelowZero_whenDebitExceedsBalance(Currency currency) {
            var balance = new Balance(AMOUNT_30, currency);
            var debitMovement = MonetaryMovement.ofDebit(MonetaryAmount.of(AMOUNT_100, currency));

            var result = balance.apply(debitMovement);

            assertThat(result.isNegative()).isTrue();
            assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("-70.00"));
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldRecoverToPositiveBalance_whenCreditIsAppliedToNegativeBalance(Currency currency) {
            var negativeBalance = new Balance(new BigDecimal("-50.00"), currency);
            var creditMovement = MonetaryMovement.ofCredit(MonetaryAmount.of(AMOUNT_100, currency));

            var result = negativeBalance.apply(creditMovement);

            assertThat(result.isNegative()).isFalse();
            assertThat(result.amount()).isEqualByComparingTo(AMOUNT_50);
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldRecoverToZero_whenCreditExactlyCoversNegativeBalance(Currency currency) {
            var negativeBalance = new Balance(new BigDecimal("-100.00"), currency);
            var creditMovement = MonetaryMovement.ofCredit(MonetaryAmount.of(AMOUNT_100, currency));

            var result = negativeBalance.apply(creditMovement);

            assertThat(result.isNegative()).isFalse();
            assertThat(result.amount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        void shouldThrowException_whenCurrenciesDoNotMatch() {
            var currency = Instancio.create(Currency.class);
            var otherCurrency = otherCurrencyThan(currency);

            var balance = new Balance(AMOUNT_100, currency);
            var movement = MonetaryMovement.ofDebit(MonetaryAmount.of(AMOUNT_50, otherCurrency));

            assertThatThrownBy(() -> balance.apply(movement))
                .isInstanceOf(CurrencyMismatchException.class)
                .satisfies(ex -> {
                    var mismatch = (CurrencyMismatchException) ex;
                    assertThat(mismatch.getExpected()).isEqualTo(currency);
                    assertThat(mismatch.getActual()).isEqualTo(otherCurrency);
                });
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldThrowException_whenMovementIsNull(Currency currency) {
            var balance = new Balance(AMOUNT_100, currency);

            assertThatThrownBy(() -> balance.apply(null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage(Balance.MONETARY_MOVEMENT_NOT_PROVIDED);
        }
    }

    @Nested
    class WhenSubtracting {

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldReturnDifference_whenCurrenciesMatch(Currency currency) {
            var balance = new Balance(AMOUNT_100, currency);
            var other = new Balance(AMOUNT_30, currency);

            var result = balance.subtract(other);

            assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("70.00"));
            assertThat(result.currency()).isEqualTo(currency);
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldAllowNegativeResult_whenOtherExceedsBalance(Currency currency) {
            var balance = new Balance(AMOUNT_30, currency);
            var other = new Balance(AMOUNT_100, currency);

            var result = balance.subtract(other);

            assertThat(result.isNegative()).isTrue();
            assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("-70.00"));
        }

        @Test
        void shouldThrowException_whenCurrenciesDoNotMatch() {
            var currency = Instancio.create(Currency.class);
            var otherCurrency = otherCurrencyThan(currency);

            var balance = new Balance(AMOUNT_100, currency);
            var other = new Balance(AMOUNT_50, otherCurrency);

            assertThatThrownBy(() -> balance.subtract(other))
                .isInstanceOf(CurrencyMismatchException.class)
                .satisfies(ex -> {
                    var mismatch = (CurrencyMismatchException) ex;
                    assertThat(mismatch.getExpected()).isEqualTo(currency);
                    assertThat(mismatch.getActual()).isEqualTo(otherCurrency);
                });
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldThrowException_whenOtherBalanceIsNull(Currency currency) {
            var balance = new Balance(AMOUNT_100, currency);

            assertThatThrownBy(() -> balance.subtract(null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage(Balance.OTHER_BALANCE_NOT_PROVIDED);
        }
    }

    @Nested
    class WhenCheckingIsNegative {

        @ParameterizedTest
        @CsvSource({"-0.01", "-1.00", "-100.00"})
        void shouldReturnTrue_whenAmountIsNegative(String amount) {
            var currency = Instancio.create(Currency.class);
            var balance = new Balance(new BigDecimal(amount), currency);

            assertThat(balance.isNegative()).isTrue();
        }

        @ParameterizedTest
        @CsvSource({"0.00", "0.01", "100.00"})
        void shouldReturnFalse_whenAmountIsNotNegative(String amount) {
            var currency = Instancio.create(Currency.class);
            var balance = new Balance(new BigDecimal(amount), currency);

            assertThat(balance.isNegative()).isFalse();
        }
    }
}
