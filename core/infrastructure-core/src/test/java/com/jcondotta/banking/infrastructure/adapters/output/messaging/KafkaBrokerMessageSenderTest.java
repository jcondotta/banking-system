package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions.BrokerMessageSendException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class KafkaBrokerMessageSenderTest {

  private static final String DESTINATION = "bank-account-opened";
  private static final String MESSAGE_KEY = "bba3f1c2-0000-4d2a-9999-111111111111";
  private static final byte[] PAYLOAD = "{\"eventType\":\"bank-account-opened\"}".getBytes(StandardCharsets.UTF_8);
  private static final Duration TIMEOUT = Duration.ofSeconds(5);

  @AfterEach
  void clearInterruptedStatus() {
    Thread.interrupted();
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldSendMessageAndWaitUsingProvidedTimeout() throws Exception {
    var kafkaTemplate = (KafkaTemplate<String, byte[]>) mock(KafkaTemplate.class);
    var sendResult = (CompletableFuture<SendResult<String, byte[]>>) mock(CompletableFuture.class);
    when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(sendResult);
    var sender = new KafkaBrokerMessageSender(kafkaTemplate);

    sender.send(new BrokerMessage(DESTINATION, MESSAGE_KEY, PAYLOAD), TIMEOUT);

    @SuppressWarnings({"rawtypes", "unchecked"})
    ArgumentCaptor<ProducerRecord<String, byte[]>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
    verify(kafkaTemplate).send(recordCaptor.capture());
    assertThat(recordCaptor.getValue().topic()).isEqualTo(DESTINATION);
    assertThat(recordCaptor.getValue().key()).isEqualTo(MESSAGE_KEY);
    assertThat(recordCaptor.getValue().value()).isEqualTo(PAYLOAD);
    verify(sendResult).get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldWrapKafkaFailure() throws Exception {
    var kafkaTemplate = (KafkaTemplate<String, byte[]>) mock(KafkaTemplate.class);
    var sendResult = (CompletableFuture<SendResult<String, byte[]>>) mock(CompletableFuture.class);
    var timeoutException = new TimeoutException("timed out");
    when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(sendResult);
    when(sendResult.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)).thenThrow(timeoutException);
    var sender = new KafkaBrokerMessageSender(kafkaTemplate);

    assertThatThrownBy(() -> sender.send(new BrokerMessage(DESTINATION, MESSAGE_KEY, PAYLOAD), TIMEOUT))
      .isInstanceOf(BrokerMessageSendException.class)
      .hasMessage("Failed to send broker message. destination=%s, key=%s".formatted(DESTINATION, MESSAGE_KEY))
      .hasCause(timeoutException);
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldRestoreInterruptedStatus_whenKafkaSendIsInterrupted() throws Exception {
    var kafkaTemplate = (KafkaTemplate<String, byte[]>) mock(KafkaTemplate.class);
    var sendResult = (CompletableFuture<SendResult<String, byte[]>>) mock(CompletableFuture.class);
    var interruptedException = new InterruptedException("interrupted");
    when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(sendResult);
    when(sendResult.get(TIMEOUT.toMillis(), TimeUnit.MILLISECONDS)).thenThrow(interruptedException);
    var sender = new KafkaBrokerMessageSender(kafkaTemplate);

    assertThatThrownBy(() -> sender.send(new BrokerMessage(DESTINATION, MESSAGE_KEY, PAYLOAD), TIMEOUT))
      .isInstanceOf(BrokerMessageSendException.class)
      .hasCause(interruptedException);
    assertThat(Thread.currentThread().isInterrupted()).isTrue();
  }
}
