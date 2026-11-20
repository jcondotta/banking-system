package com.jcondotta.banking.ledger.infrastructure.adapters.input.messaging;

import com.jcondotta.banking.infrastructure.adapters.input.messaging.EventMessage;
import com.jcondotta.banking.ledger.application.ledger_account.command.apply_transfer.ApplyInternalTransferCommand;
import com.jcondotta.banking.ledger.application.ledger_account.command.apply_transfer.ApplyInternalTransferCommandHandler;
import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.movement.MovementAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InternalTransferRequestedEventConsumer {

    private static final TypeReference<EventMessage<InternalTransferRequestedData>> MESSAGE_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;
    private final ApplyInternalTransferCommandHandler commandHandler;

    @KafkaListener(
            topics = "${app.kafka.topics.internal-transfer-requested}",
            groupId = "${app.kafka.consumer.transfer-group-id}",
            containerFactory = "ledgerKafkaListenerContainerFactory"
    )
    public void consume(byte[] payload) {
        var json = new String(payload, StandardCharsets.UTF_8);
        var message = objectMapper.readValue(json, MESSAGE_TYPE);

        var data = message.data();
        var movementAmount = MovementAmount.of(
                data.amount(),
                Currency.valueOf(data.currency())
        );

        var command = new ApplyInternalTransferCommand(
                UUID.fromString(message.aggregateId()),
                data.senderAccountId(),
                data.recipientAccountId(),
                movementAmount,
                message.occurredAt()
        );

        commandHandler.handle(command);
    }
}
