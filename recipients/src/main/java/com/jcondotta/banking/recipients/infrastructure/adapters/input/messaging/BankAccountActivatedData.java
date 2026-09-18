package com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging;

public record BankAccountActivatedData(String iban, String currency) {
}
