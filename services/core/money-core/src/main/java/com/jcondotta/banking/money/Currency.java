package com.jcondotta.banking.money;

import com.jcondotta.banking.money.exception.CurrencyMismatchException;

public enum Currency {
    EUR("Euro", "€", 2),
    USD("US Dollar", "$", 2);

    private final String description;
    private final String symbol;
    private final int scale;

    Currency(String description, String symbol, int scale) {
        this.description = description;
        this.symbol = symbol;
        this.scale = scale;
    }

    public String symbol() {
        return symbol;
    }

    public String description() {
        return description;
    }

    public int scale() {
        return scale;
    }

    public void requireSameAs(Currency actual) {
        if (this != actual) {
            throw new CurrencyMismatchException(this, actual);
        }
    }
}
