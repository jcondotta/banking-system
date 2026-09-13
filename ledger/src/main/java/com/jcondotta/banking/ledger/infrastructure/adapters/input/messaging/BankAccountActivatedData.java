package com.jcondotta.banking.ledger.infrastructure.adapters.input.messaging;

public record BankAccountActivatedData(String iban, String currency) {}
