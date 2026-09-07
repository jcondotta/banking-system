package com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.domain.exception.DomainValidationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountBalanceTest {

    @Nested
    class WhenCreatingZeroAccountBalance {

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldCreateZeroBookedAndHeldBalances_whenCurrencyIsValid(Currency currency) {
            var balance = AccountBalance.zero(currency);

            assertThat(balance.booked().amount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(balance.booked().currency()).isEqualTo(currency);
            assertThat(balance.held().amount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(balance.held().currency()).isEqualTo(currency);
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldReturnZeroAvailable_whenBookedAndHeldAreZero(Currency currency) {
            var balance = AccountBalance.zero(currency);

            assertThat(balance.available().amount()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(balance.available().currency()).isEqualTo(currency);
        }

        @Test
        void shouldThrowException_whenCurrencyIsNull() {
            assertThatThrownBy(() -> AccountBalance.zero(null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage(Balance.CURRENCY_NOT_PROVIDED);
        }
    }

    @Nested
    class WhenComputingAvailable {

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldReturnBookedMinusHeld_whenBalancesHaveSameCurrency(Currency currency) {
            var booked = new Balance(new BigDecimal("100.00"), currency);
            var held = new Balance(new BigDecimal("30.00"), currency);
            var balance = new AccountBalance(booked, held);

            var available = balance.available();

            assertThat(available.amount()).isEqualByComparingTo(new BigDecimal("70.00"));
            assertThat(available.currency()).isEqualTo(currency);
        }

        @ParameterizedTest
        @EnumSource(Currency.class)
        void shouldReturnNegativeAvailable_whenHeldExceedsBooked(Currency currency) {
            var booked = new Balance(new BigDecimal("50.00"), currency);
            var held = new Balance(new BigDecimal("80.00"), currency);
            var balance = new AccountBalance(booked, held);

            var available = balance.available();

            assertThat(available.isNegative()).isTrue();
            assertThat(available.amount()).isEqualByComparingTo(new BigDecimal("-30.00"));
        }
    }

    @Nested
    class WhenCreatingAccountBalance {

        @Test
        void shouldThrowException_whenBookedBalanceIsNull() {
            var currency = Instancio.create(Currency.class);
            var held = Balance.zero(currency);

            assertThatThrownBy(() -> new AccountBalance(null, held))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage(AccountBalance.BOOKED_BALANCE_NOT_PROVIDED);
        }

        @Test
        void shouldThrowException_whenHeldBalanceIsNull() {
            var currency = Instancio.create(Currency.class);
            var booked = Balance.zero(currency);

            assertThatThrownBy(() -> new AccountBalance(booked, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessage(AccountBalance.HELD_BALANCE_NOT_PROVIDED);
        }
    }
}
