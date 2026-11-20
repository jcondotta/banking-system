package com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging;

import com.jcondotta.banking.infrastructure.adapters.input.messaging.EventMessage;
import com.jcondotta.banking.recipients.application.bank_account.command.register.RegisterActivatedBankAccountCommand;
import com.jcondotta.banking.recipients.application.bank_account.command.register.RegisterActivatedBankAccountCommandHandler;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class BankAccountActivatedEventConsumer {

  private static final TypeReference<EventMessage<BankAccountActivatedData>> MESSAGE_TYPE = new TypeReference<>() {};

  private final ObjectMapper objectMapper;
  private final RegisterActivatedBankAccountCommandHandler commandHandler;

  public BankAccountActivatedEventConsumer(ObjectMapper objectMapper, RegisterActivatedBankAccountCommandHandler commandHandler) {
    this.objectMapper = objectMapper;
    this.commandHandler = commandHandler;
  }

  @KafkaListener(
    topics = "${app.kafka.topics.bank-account-activated.topic-name}",
    groupId = "${app.kafka.consumer.group-id}",
    containerFactory = "recipientsKafkaListenerContainerFactory"
  )
  public void consume(byte[] payload) {
    var message = objectMapper.readValue(payload, MESSAGE_TYPE);
    var command = new RegisterActivatedBankAccountCommand(
      BankAccountId.of(UUID.fromString(message.aggregateId()))
    );

    commandHandler.handle(command);
  }
}
