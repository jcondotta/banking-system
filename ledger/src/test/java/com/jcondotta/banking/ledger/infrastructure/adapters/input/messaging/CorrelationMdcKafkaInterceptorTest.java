package com.jcondotta.banking.ledger.infrastructure.adapters.input.messaging;

import com.jcondotta.application.logging.LogKey;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorrelationMdcKafkaInterceptorTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Consumer<String, byte[]> consumer;

    private CorrelationMdcKafkaInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new CorrelationMdcKafkaInterceptor(objectMapper);
    }

    @AfterEach
    void tearDown() {
        MDC.remove(LogKey.CORRELATION_ID);
    }

    @Test
    void shouldSetCorrelationIdInMdc_whenPayloadContainsCorrelationId() {
        var correlationId = UUID.randomUUID();
        var record = buildRecord();

        when(objectMapper.readValue(any(String.class), eq(CorrelationEnvelope.class)))
                .thenReturn(new CorrelationEnvelope(correlationId));

        interceptor.intercept(record, consumer);

        assertSoftly(softly ->
                softly.assertThat(MDC.get(LogKey.CORRELATION_ID)).isEqualTo(correlationId.toString())
        );
    }

    @Test
    void shouldNotSetMdc_whenCorrelationIdIsNull() {
        var record = buildRecord();

        when(objectMapper.readValue(any(String.class), eq(CorrelationEnvelope.class)))
                .thenReturn(new CorrelationEnvelope(null));

        interceptor.intercept(record, consumer);

        assertSoftly(softly ->
                softly.assertThat(MDC.get(LogKey.CORRELATION_ID)).isNull()
        );
    }

    @Test
    void shouldNotThrow_andNotSetMdc_whenPayloadDeserializationFails() {
        var record = buildRecord();

        when(objectMapper.readValue(any(String.class), eq(CorrelationEnvelope.class)))
                .thenThrow(new RuntimeException("parse error"));

        assertThatCode(() -> interceptor.intercept(record, consumer)).doesNotThrowAnyException();

        assertSoftly(softly ->
                softly.assertThat(MDC.get(LogKey.CORRELATION_ID)).isNull()
        );
    }

    @Test
    void shouldRemoveCorrelationIdFromMdc_onSuccess() {
        MDC.put(LogKey.CORRELATION_ID, UUID.randomUUID().toString());
        var record = buildRecord();

        interceptor.success(record, consumer);

        assertSoftly(softly ->
                softly.assertThat(MDC.get(LogKey.CORRELATION_ID)).isNull()
        );
    }

    @Test
    void shouldRemoveCorrelationIdFromMdc_onFailure() {
        MDC.put(LogKey.CORRELATION_ID, UUID.randomUUID().toString());
        var record = buildRecord();

        interceptor.failure(record, new RuntimeException("consumer error"), consumer);

        assertSoftly(softly ->
                softly.assertThat(MDC.get(LogKey.CORRELATION_ID)).isNull()
        );
    }

    private ConsumerRecord<String, byte[]> buildRecord() {
        return new ConsumerRecord<>("test-topic", 0, 0L, "key", "{}".getBytes());
    }
}
