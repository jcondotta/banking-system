package com.jcondotta.banking.transfers.domain.movement;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.domain.testsupport.MovementTypeAndCurrencyArgumentsProvider;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MovementTest {

    private static final BigDecimal AMOUNT_200 = new BigDecimal("200.00");

    @ParameterizedTest
    @ArgumentsSource(MovementTypeAndCurrencyArgumentsProvider.class)
    void shouldCreateMovement_whenRequestIsValid(MovementType movementType, Currency currency) {
        var movementAmount = MovementAmount.of(AMOUNT_200, currency);
        var movement = Movement.of(movementType, movementAmount);

        assertThat(movement)
            .satisfies(m -> {
                assertThat(m.movementType()).isEqualTo(movementType);
                assertThat(m.isDebit()).isEqualTo(movementType.isDebit());
                assertThat(m.isCredit()).isEqualTo(movementType.isCredit());
                assertThat(m.currency()).isEqualTo(currency);
                assertThat(m.amount()).isEqualTo(AMOUNT_200);
                assertThat(m.movementAmount()).satisfies(amount -> {
                    assertThat(amount.currency()).isEqualTo(currency);
                    assertThat(amount.amount()).isEqualTo(AMOUNT_200);
                });
            });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateDebitMovement_whenUsingOfDebitFactory(Currency currency) {
        var movementAmount = MovementAmount.of(AMOUNT_200, currency);
        var debitMovement = Movement.ofDebit(movementAmount);

        assertThat(debitMovement)
            .satisfies(m -> {
                assertThat(m.movementType()).isEqualTo(MovementType.DEBIT);
                assertThat(m.isDebit()).isTrue();
                assertThat(m.isCredit()).isFalse();
                assertThat(m.currency()).isEqualTo(currency);
                assertThat(m.amount()).isEqualTo(AMOUNT_200);
            });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateCreditMovement_whenUsingOfCreditFactory(Currency currency) {
        var movementAmount = MovementAmount.of(AMOUNT_200, currency);
        var creditMovement = Movement.ofCredit(movementAmount);

        assertThat(creditMovement)
            .satisfies(m -> {
                assertThat(m.movementType()).isEqualTo(MovementType.CREDIT);
                assertThat(m.isCredit()).isTrue();
                assertThat(m.isDebit()).isFalse();
                assertThat(m.currency()).isEqualTo(currency);
                assertThat(m.amount()).isEqualTo(AMOUNT_200);
            });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldThrowException_whenMovementTypeIsNull(Currency currency) {
        var movementAmount = MovementAmount.of(AMOUNT_200, currency);

        assertThatThrownBy(() -> Movement.of(null, movementAmount))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Movement.MOVEMENT_TYPE_NOT_PROVIDED);
    }

    @ParameterizedTest
    @EnumSource(MovementType.class)
    void shouldThrowException_whenMovementAmountIsNull(MovementType movementType) {
        assertThatThrownBy(() -> Movement.of(movementType, null))
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(Movement.MOVEMENT_AMOUNT_NOT_PROVIDED);
    }
}
