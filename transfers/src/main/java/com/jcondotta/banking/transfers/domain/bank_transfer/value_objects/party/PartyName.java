package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party;

import static com.jcondotta.domain.support.Preconditions.checkArgument;
import static com.jcondotta.domain.support.Preconditions.required;

public record PartyName(String value) {

    public static final String NAME_NOT_PROVIDED = "name must be provided.";
    public static final String NAME_NOT_BLANK = "name must not be blank.";
    public static final String NAME_TOO_LONG = "name must not exceed 255 characters.";

    public PartyName {
        required(value, NAME_NOT_PROVIDED);
        checkArgument(!value.trim().isBlank(), NAME_NOT_BLANK);
        checkArgument(value.trim().length() <= 255, NAME_TOO_LONG);

        value = value.trim();
    }

    public static PartyName of(String value) {
        return new PartyName(value);
    }
}
