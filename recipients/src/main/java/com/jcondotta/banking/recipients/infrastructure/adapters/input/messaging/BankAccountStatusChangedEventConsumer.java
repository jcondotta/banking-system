package com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging;

import com.jcondotta.banking.infrastructure.adapters.input.messaging.EventMessage;
import com.jcondotta.banking.recipients.application.bank_account.command.update.UpdateBankAccountStatusCommand;
import com.jcondotta.banking.recipients.application.bank_account.command.update.UpdateBankAccountStatusCommandHandler;
import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class BankAccountStatusChangedEventConsumer {

  private static final TypeReference<EventMessage<BankAccountStatusChangedData>> MESSAGE_TYPE = new TypeReference<>() {};

  private final ObjectMapper objectMapper;
  private final UpdateBankAccountStatusCommandHandler commandHandler;

  public BankAccountStatusChangedEventConsumer(ObjectMapper objectMapper, UpdateBankAccountStatusCommandHandler commandHandler) {
    this.objectMapper = objectMapper;
    this.commandHandler = commandHandler;
  }

  @KafkaListener(
    topics = "${app.kafka.topics.bank-account-status-changed.topic-name}",
    groupId = "${app.kafka.consumer.group-id}",
    containerFactory = "recipientsKafkaListenerContainerFactory"
  )
  public void consume(byte[] payload) {
    var message = objectMapper.readValue(payload, MESSAGE_TYPE);
    var command = new UpdateBankAccountStatusCommand(
      BankAccountId.of(UUID.fromString(message.aggregateId())),
      BankAccountStatus.valueOf(message.data().currentStatus())
    );

    commandHandler.handle(command);
  }
}
