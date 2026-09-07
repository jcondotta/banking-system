package com.jcondotta.banking.transfers.ledger.infrastructure.adapters.input.messaging;

import com.jcondotta.banking.transfers.ledger.application.ledger_account.command.provision.ProvisionLedgerAccountCommand;
import com.jcondotta.banking.transfers.ledger.application.ledger_account.command.provision.ProvisionLedgerAccountCommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountActivatedEventConsumerTest {

    private static final UUID AGGREGATE_ID = UUID.randomUUID();
    private static final String IBAN = "GB29NWBK60161331926819";
    private static final String CURRENCY = "EUR";
    private static final Instant OCCURRED_AT = Instant.parse("2026-05-16T10:15:30Z");

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProvisionLedgerAccountCommandHandler commandHandler;

    @Captor
    private ArgumentCaptor<ProvisionLedgerAccountCommand> commandCaptor;

    private BankAccountActivatedEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new BankAccountActivatedEventConsumer(objectMapper, commandHandler);
    }

    @Test
    void shouldDelegateToCommandHandler_whenPayloadDeserialized() {
        var message = buildMessage();
        when(objectMapper.readValue(any(String.class), eq(BankAccountActivatedMessage.class))).thenReturn(message);

        consumer.consume("{}".getBytes());

        verify(commandHandler).handle(commandCaptor.capture());
        var command = commandCaptor.getValue();

        assertThat(command.accountId()).isEqualTo(AGGREGATE_ID);
        assertThat(command.iban()).isEqualTo(IBAN);
        assertThat(command.currency()).isEqualTo(CURRENCY);
        assertThat(command.activatedAt()).isEqualTo(OCCURRED_AT);
    }

    private BankAccountActivatedMessage buildMessage() {
        var data = new BankAccountActivatedMessage.BankAccountActivatedMessageData(IBAN, CURRENCY);
        return new BankAccountActivatedMessage(
                UUID.randomUUID().toString(),
                UUID.randomUUID(),
                AGGREGATE_ID.toString(),
                "bank-account-activated",
                "accounts",
                OCCURRED_AT,
                1,
                data
        );
    }
}
