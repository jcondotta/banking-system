package com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.transfers.domain.bank_transfer.events.InternalTransferCompletedEvent;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties.TopicConfig;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InternalTransferCompletedEventRoutingResolverTest {

    private static final BankTransferId BANK_TRANSFER_ID =
        BankTransferId.of(UUID.fromString("2dafefd6-4cf8-420e-a99a-754d947e1021"));
    private static final KafkaTopicsProperties TOPICS_PROPERTIES = new KafkaTopicsProperties(
        new TopicConfig("custom-internal-transfer-requested"),
        new TopicConfig("custom-internal-transfer-completed")
    );
    private final InternalTransferCompletedEventRoutingResolver resolver =
        new InternalTransferCompletedEventRoutingResolver(TOPICS_PROPERTIES);

    @Test
    void shouldCreateRouting_whenInternalTransferCompletedEvent() {
        var event = new InternalTransferCompletedEvent(
            EventId.of(UUID.fromString("f95fc962-a161-4116-946b-30b6ca6d2694")),
            BANK_TRANSFER_ID,
            Instant.parse("2026-09-06T10:00:00Z")
        );

        var routing = resolver.resolve(event);

        assertThat(resolver.domainEventType()).isEqualTo(InternalTransferCompletedEvent.class);
        assertThat(routing.destination()).isEqualTo("custom-internal-transfer-completed");
        assertThat(routing.key()).isEqualTo(BANK_TRANSFER_ID.asString());
    }
}
