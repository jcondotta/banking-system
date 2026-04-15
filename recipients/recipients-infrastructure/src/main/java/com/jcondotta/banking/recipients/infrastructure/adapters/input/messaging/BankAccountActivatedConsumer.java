package com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.contracts.activate.BankAccountActivatedIntegrationEvent;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class BankAccountActivatedConsumer {

  private final CommandHandler<RegisterBankAccountCommand> handler;
  private final ObjectMapper objectMapper;

  @KafkaListener(
    topics = "${app.kafka.topics.bank-account-activated.topic-name}",
    groupId = "${app.kafka.topics.bank-account-activated.group-id}"
  )
  public void consume(String message) {
    BankAccountActivatedIntegrationEvent event;
    try {
      event = objectMapper.readValue(message, BankAccountActivatedIntegrationEvent.class);
    }
    catch (JacksonException e) {
      throw new IllegalArgumentException("Invalid message payload", e);
    }

    var command = new RegisterBankAccountCommand(
      BankAccountId.of(event.payload().bankAccountId())
    );

    handler.handle(command);
  }
}
