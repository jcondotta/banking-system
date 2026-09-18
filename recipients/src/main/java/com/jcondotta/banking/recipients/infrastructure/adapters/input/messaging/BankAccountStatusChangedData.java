package com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging;

public record BankAccountStatusChangedData(String previousStatus, String currentStatus) {
}
