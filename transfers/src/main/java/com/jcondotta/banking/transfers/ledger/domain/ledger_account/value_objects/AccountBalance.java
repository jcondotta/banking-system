package com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects;

import com.jcondotta.banking.money.Currency;

import static com.jcondotta.domain.support.Preconditions.required;

public record AccountBalance(Balance booked, Balance held) {

    public static final String BOOKED_BALANCE_NOT_PROVIDED = "booked balance must be provided.";
    public static final String HELD_BALANCE_NOT_PROVIDED = "held balance must be provided.";

    public AccountBalance {
        required(booked, BOOKED_BALANCE_NOT_PROVIDED);
        required(held, HELD_BALANCE_NOT_PROVIDED);
    }

    public static AccountBalance zero(Currency currency) {
        return new AccountBalance(Balance.zero(currency), Balance.zero(currency));
    }

    public Balance available() {
        return booked.subtract(held);
    }
}
