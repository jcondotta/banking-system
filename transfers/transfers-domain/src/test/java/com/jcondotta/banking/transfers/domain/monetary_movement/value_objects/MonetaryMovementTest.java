package com.jcondotta.banking.transfers.domain.monetary_movement.value_objects;

import com.jcondotta.banking.transfers.domain.monetary_movement.enums.MovementType;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;
import com.jcondotta.banking.transfers.domain.testsupport.MovementTypeAndCurrencyArgumentsProvider;
import com.jcondotta.domain.exception.InvalidDomainDataException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MonetaryMovementTest {

    private static final BigDecimal AMOUNT_200 = new BigDecimal("200.00");

    @ParameterizedTest
    @ArgumentsSource(MovementTypeAndCurrencyArgumentsProvider.class)
    void shouldCreateMonetaryMovement_whenRequestIsValid(MovementType movementType, Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);
        var monetaryMovement = MonetaryMovement.of(movementType, monetaryAmount);

        assertThat(monetaryMovement)
            .satisfies(movement -> {
                assertThat(movement.movementType()).isEqualTo(movementType);
                assertThat(movement.isDebit()).isEqualTo(movementType.isDebit());
                assertThat(movement.isCredit()).isEqualTo(movementType.isCredit());
                assertThat(movement.currency()).isEqualTo(currency);
                assertThat(movement.amount()).isEqualTo(AMOUNT_200);
                assertThat(movement.monetaryAmount()).satisfies(amount -> {
                    assertThat(amount.currency()).isEqualTo(currency);
                    assertThat(amount.amount()).isEqualTo(AMOUNT_200);
                });
            });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateDebitMonetaryMovement_whenUsingOfDebitFactory(Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);
        var debitMovement = MonetaryMovement.ofDebit(monetaryAmount);

        assertThat(debitMovement)
            .satisfies(movement -> {
                assertThat(movement.movementType()).isEqualTo(MovementType.DEBIT);
                assertThat(movement.isDebit()).isTrue();
                assertThat(movement.isCredit()).isFalse();
                assertThat(movement.currency()).isEqualTo(currency);
                assertThat(movement.amount()).isEqualTo(AMOUNT_200);
            });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateCreditMonetaryMovement_whenUsingOfCreditFactory(Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);
        var creditMovement = MonetaryMovement.ofCredit(monetaryAmount);

        assertThat(creditMovement)
            .satisfies(movement -> {
                assertThat(movement.movementType()).isEqualTo(MovementType.CREDIT);
                assertThat(movement.isCredit()).isTrue();
                assertThat(movement.isDebit()).isFalse();
                assertThat(movement.currency()).isEqualTo(currency);
                assertThat(movement.amount()).isEqualTo(AMOUNT_200);
            });
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldThrowException_whenMovementTypeIsNull(Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);

        assertThatThrownBy(() -> MonetaryMovement.of(null, monetaryAmount))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(MonetaryMovement.MOVEMENT_TYPE_NOT_PROVIDED);
    }

    @ParameterizedTest
    @EnumSource(MovementType.class)
    void shouldThrowException_whenMonetaryAmountIsNull(MovementType movementType) {
        assertThatThrownBy(() -> MonetaryMovement.of(movementType, null))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(MonetaryMovement.MONETARY_AMOUNT_NOT_PROVIDED);
    }
}
