package com.jcondotta.banking.transfers.ledger.infrastructure.adapters.input.messaging;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.domain.movement.MovementAmount;
import com.jcondotta.banking.transfers.ledger.application.ledger_account.command.apply_transfer.ApplyInternalTransferCommand;
import com.jcondotta.banking.transfers.ledger.application.ledger_account.command.apply_transfer.ApplyInternalTransferCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

// @Component
@RequiredArgsConstructor
public class InternalTransferRequestedEventConsumer {

    private final ObjectMapper objectMapper;
    private final ApplyInternalTransferCommandHandler commandHandler;

    @KafkaListener(
            topics = "${app.ledger.kafka.topics.internal-transfer-requested}",
            groupId = "${app.ledger.kafka.consumer.transfer-group-id}",
            containerFactory = "ledgerKafkaListenerContainerFactory"
    )
    public void consume(byte[] payload) {
        var json = new String(payload, StandardCharsets.UTF_8);
        var message = objectMapper.readValue(json, InternalTransferRequestedMessage.class);

//        var monetaryData = message.data().monetaryAmount();
//        var movementAmount = MovementAmount.of(
//                monetaryData.amount(),
//                Currency.valueOf(monetaryData.currency())
//        );
//
//        var command = new ApplyInternalTransferCommand(
//                UUID.fromString(message.aggregateId()),
//                message.data().senderAccountId(),
//                message.data().recipientAccountId(),
//                movementAmount,
//                message.occurredAt()
//        );
//
//        commandHandler.handle(command);
    }
}
