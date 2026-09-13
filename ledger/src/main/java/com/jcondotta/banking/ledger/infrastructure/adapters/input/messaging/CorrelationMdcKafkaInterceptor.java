package com.jcondotta.banking.ledger.infrastructure.adapters.input.messaging;

import com.jcondotta.application.logging.LogKey;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CorrelationMdcKafkaInterceptor implements RecordInterceptor<String, byte[]> {

    private final ObjectMapper objectMapper;

    @Override
    public ConsumerRecord<String, byte[]> intercept(ConsumerRecord<String, byte[]> record, Consumer<String, byte[]> consumer) {
        try {
            var json = new String(record.value(), StandardCharsets.UTF_8);
            var envelope = objectMapper.readValue(json, CorrelationEnvelope.class);
            if (envelope.correlationId() != null) {
                MDC.put(LogKey.CORRELATION_ID, envelope.correlationId().toString());
            }
        }
        catch (Exception ignored) {
        }
        return record;
    }

    @Override
    public void success(ConsumerRecord<String, byte[]> record, Consumer<String, byte[]> consumer) {
        MDC.remove(LogKey.CORRELATION_ID);
    }

    @Override
    public void failure(ConsumerRecord<String, byte[]> record, Exception exception, Consumer<String, byte[]> consumer) {
        MDC.remove(LogKey.CORRELATION_ID);
    }
}
