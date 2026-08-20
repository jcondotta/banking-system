package com.jcondotta.banking.transfers.domain.shared.value_objects;

public enum Currency {
    EUR("Euro", "€"),
    USD("US Dollar", "$");

    private final String description;
    private final String symbol;

    Currency(String description, String symbol) {
        this.description = description;
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

    public String description() {
        return description;
    }
}
