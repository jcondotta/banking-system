package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open;

import org.springframework.stereotype.Component;

@Component
public class TestKafkaListener {

//  private final BlockingQueue<ConsumerRecord<String, EventEnvelope<BankAccountOpenedIntegrationPayload>>> records =
//      new LinkedBlockingQueue<>();
//
//  @KafkaListener(
//      topics = "${KAFKA_BANK_ACCOUNT_OPENED_TOPIC_NAME}",
//      groupId = "test-bank-account-opened-consumer"
//  )
//  public void listen(ConsumerRecord<String, EventEnvelope<BankAccountOpenedIntegrationPayload>> record) {
//    records.add(record);
//  }
//
//  public ConsumerRecord<String, EventEnvelope<BankAccountOpenedIntegrationPayload>> poll(long timeout, TimeUnit unit)
//      throws InterruptedException {
//    return records.poll(timeout, unit);
//  }
//
//  public void clear() {
//    records.clear();
//  }
}
