package com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.money.MonetaryMovement;
import com.jcondotta.banking.money.exception.CurrencyMismatchException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.jcondotta.domain.support.Preconditions.required;

public record Balance(BigDecimal amount, Currency currency) {

    public static final String AMOUNT_NOT_PROVIDED = "balance amount must be provided.";
    public static final String CURRENCY_NOT_PROVIDED = "balance currency must be provided.";
    public static final String MONETARY_MOVEMENT_NOT_PROVIDED = "monetary movement must be provided.";
    public static final String OTHER_BALANCE_NOT_PROVIDED = "balance to subtract must be provided.";

    public Balance {
        required(amount, AMOUNT_NOT_PROVIDED);
        required(currency, CURRENCY_NOT_PROVIDED);

        amount = amount.setScale(currency.scale(), RoundingMode.HALF_UP);
    }

    public static Balance zero(Currency currency) {
        return new Balance(BigDecimal.ZERO, currency);
    }

    public Balance apply(MonetaryMovement movement) {
        required(movement, MONETARY_MOVEMENT_NOT_PROVIDED);
        requireSameCurrency(movement.currency());

        BigDecimal newAmount = movement.isDebit()
            ? this.amount.subtract(movement.amount())
            : this.amount.add(movement.amount());

        return new Balance(newAmount, this.currency);
    }

    public Balance subtract(Balance other) {
        required(other, OTHER_BALANCE_NOT_PROVIDED);
        requireSameCurrency(other.currency());

        return new Balance(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    private void requireSameCurrency(Currency other) {
        if (!other.equals(this.currency)) {
            throw new CurrencyMismatchException(this.currency, other);
        }
    }
}
