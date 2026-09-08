package com.jcondotta.banking.transfers.ledger.infrastructure.adapters.input.messaging;

import com.jcondotta.banking.transfers.ledger.application.ledger_account.command.provision.ProvisionLedgerAccountCommand;
import com.jcondotta.banking.transfers.ledger.application.ledger_account.command.provision.ProvisionLedgerAccountCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

// @Component
@RequiredArgsConstructor
public class BankAccountActivatedEventConsumer {

    private final ObjectMapper objectMapper;
    private final ProvisionLedgerAccountCommandHandler commandHandler;

    @KafkaListener(
            topics = "${app.ledger.kafka.topics.bank-account-activated}",
            groupId = "${app.ledger.kafka.consumer.group-id}",
            containerFactory = "ledgerKafkaListenerContainerFactory"
    )
    public void consume(byte[] payload) {
        var json = new String(payload, StandardCharsets.UTF_8);
        var message = objectMapper.readValue(json, BankAccountActivatedMessage.class);

        var command = new ProvisionLedgerAccountCommand(
                UUID.fromString(message.aggregateId()),
                message.data().iban(),
                message.data().currency(),
                message.occurredAt()
        );

        commandHandler.handle(command);
    }
}
