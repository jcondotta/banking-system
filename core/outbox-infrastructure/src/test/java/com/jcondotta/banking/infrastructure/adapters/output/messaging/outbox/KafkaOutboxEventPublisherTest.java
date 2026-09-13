package com.jcondotta.banking.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.BrokerMessage;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.BrokerMessageSender;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions.BrokerMessageSendException;
import com.jcondotta.banking.infrastructure.outbox.exceptions.OutboxPublishException;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaOutboxEventPublisherTest {

  private static final String DESTINATION = "bank-account-opened";
  private static final String MESSAGE_KEY = "bba3f1c2-0000-4d2a-9999-111111111111";
  private static final String PAYLOAD_JSON = "{\"eventId\":\"9f1c2a44-6b7e-4c1a-8d3e-2f7a9b6c5d01\",\"eventType\":\"bank-account-opened\"}";
  private static final Duration PUBLISH_TIMEOUT = Duration.ofSeconds(5);

  @Mock
  private BrokerMessageSender messageSender;

  @Mock
  private OutboxProperties outboxProperties;

  @Mock
  private OutboxRecord outboxRecord;

  private KafkaOutboxEventPublisher<OutboxRecord> publisher;

  @BeforeEach
  void setUp() {
    publisher = new KafkaOutboxEventPublisher<>(messageSender, outboxProperties);

    when(outboxRecord.getDestination()).thenReturn(DESTINATION);
    when(outboxRecord.getMessageKey()).thenReturn(MESSAGE_KEY);
    when(outboxRecord.getPayload()).thenReturn(PAYLOAD_JSON);

    var processing = mock(OutboxProperties.Worker.Processing.class);
    var worker = mock(OutboxProperties.Worker.class);
    when(outboxProperties.worker()).thenReturn(worker);
    when(worker.processing()).thenReturn(processing);
    when(processing.publishTimeout()).thenReturn(PUBLISH_TIMEOUT);
  }

  @Test
  void shouldSendOutboxPayloadThroughSharedBrokerSender() {
    var messageCaptor = ArgumentCaptor.forClass(BrokerMessage.class);

    publisher.send(outboxRecord);

    verify(messageSender).send(messageCaptor.capture(), eq(PUBLISH_TIMEOUT));
    var message = messageCaptor.getValue();
    assertThat(message.destination()).isEqualTo(DESTINATION);
    assertThat(message.key()).isEqualTo(MESSAGE_KEY);
    assertThat(message.payload()).isEqualTo(PAYLOAD_JSON.getBytes(StandardCharsets.UTF_8));
    verifyNoMoreInteractions(messageSender);
  }

  @Test
  void shouldPreserveOriginalCause_whenSharedBrokerSenderFails() {
    var timeoutException = new TimeoutException("timed out");
    doThrow(new BrokerMessageSendException(DESTINATION, MESSAGE_KEY, timeoutException))
      .when(messageSender).send(any(), any());

    assertThatThrownBy(() -> publisher.send(outboxRecord))
      .isInstanceOf(OutboxPublishException.class)
      .hasMessage("Failed to publish event to Kafka. topic=%s, messageKey=%s".formatted(DESTINATION, MESSAGE_KEY))
      .hasCause(timeoutException);
  }
}
