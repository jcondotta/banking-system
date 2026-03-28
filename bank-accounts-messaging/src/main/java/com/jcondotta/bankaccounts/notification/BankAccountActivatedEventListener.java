package com.jcondotta.bankaccounts.notification;

import com.jcondotta.bankaccounts.contracts.activate.BankAccountActivatedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankAccountActivatedEventListener {

  private final ObjectMapper objectMapper;
  private final SendBankAccountActivatedNotificationUseCase useCase;

  @KafkaListener(
    topics = "bank-account-activated",
    groupId = "bank-accounts-notifications"
  )
  public void handle(BankAccountActivatedIntegrationEvent integrationEvent) throws JsonProcessingException {

    log.info("Received raw message: {}", integrationEvent);

//    JavaType envelopeType = objectMapper
//      .getTypeFactory()
//      .constructParametricType(
//        EventEnvelope.class,
//        BankAccountActivatedEvent.class
//      );
//
//    EventEnvelope<BankAccountActivatedEvent> envelope =
//      objectMapper.readValue(rawMessage, envelopeType);


    useCase.execute(integrationEvent.payload());
  }

}
