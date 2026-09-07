package com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.publication;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.money.MonetaryAmount;
import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.events.InternalTransferRequestedEvent;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties.TopicConfig;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InternalTransferRequestedEventPublicationFactoryTest {

    private static final BankTransferId BANK_TRANSFER_ID =
        BankTransferId.of(UUID.fromString("2dafefd6-4cf8-420e-a99a-754d947e1021"));
    private static final KafkaTopicsProperties TOPICS_PROPERTIES = new KafkaTopicsProperties(
        new TopicConfig("custom-internal-transfer-requested"),
        new TopicConfig("custom-internal-transfer-completed")
    );
    private final InternalTransferRequestedEventPublicationFactory factory =
        new InternalTransferRequestedEventPublicationFactory(TOPICS_PROPERTIES);

    @Test
    void shouldCreatePublication_whenInternalTransferRequestedEvent() {
        var event = new InternalTransferRequestedEvent(
            EventId.of(UUID.fromString("f95fc962-a161-4116-946b-30b6ca6d2694")),
            BANK_TRANSFER_ID,
            BankAccountId.of(UUID.fromString("0b89f13d-4990-4be1-9868-ae3acbbcb9d8")),
            BankAccountId.of(UUID.fromString("70874b4d-3b81-4dfa-a92d-519c82022b1b")),
            MonetaryAmount.of(new BigDecimal("200.00"), Currency.EUR),
            "Invoice 123",
            Instant.parse("2026-09-06T10:00:00Z")
        );

        var publication = factory.create(event);

        assertThat(factory.domainEventType()).isEqualTo(InternalTransferRequestedEvent.class);
        assertThat(publication.event()).isSameAs(event);
        assertThat(publication.destination()).isEqualTo("custom-internal-transfer-requested");
        assertThat(publication.key()).isEqualTo(BANK_TRANSFER_ID.asString());
    }
}
