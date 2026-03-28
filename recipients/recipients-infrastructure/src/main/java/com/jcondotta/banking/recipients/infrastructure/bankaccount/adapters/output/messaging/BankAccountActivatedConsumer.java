package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.messaging;

import com.jcondotta.banking.contracts.activate.BankAccountActivatedIntegrationEvent;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommand;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommandHandler;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class BankAccountActivatedConsumer {

  private final RegisterBankAccountCommandHandler handler;
  private final ObjectMapper objectMapper;

  @KafkaListener(
    topics = "${app.kafka.topics.bank-account-activated.topic-name}",
    groupId = "recipients-service"
  )
  public void consume(String message) {
    var event = objectMapper.readValue(message, BankAccountActivatedIntegrationEvent.class);

    var command = new RegisterBankAccountCommand(
      new BankAccountId(event.payload().bankAccountId())
    );

    handler.handle(command);
  }
}