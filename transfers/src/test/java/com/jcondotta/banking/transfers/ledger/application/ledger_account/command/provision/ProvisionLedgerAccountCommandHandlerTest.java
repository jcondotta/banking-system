package com.jcondotta.banking.transfers.ledger.application.ledger_account.command.provision;

import com.jcondotta.banking.transfers.ledger.domain.ledger_account.aggregate.LedgerAccount;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.enums.LedgerAccountStatus;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.enums.LedgerAccountType;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.repository.LedgerAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ProvisionLedgerAccountCommandHandlerTest {

    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final String IBAN = "GB29NWBK60161331926819";
    private static final String CURRENCY = "EUR";
    private static final Instant ACTIVATED_AT = Instant.parse("2026-05-16T10:15:30Z");

    @Mock
    private LedgerAccountRepository ledgerAccountRepository;

    @Captor
    private ArgumentCaptor<LedgerAccount> ledgerAccountCaptor;

    private ProvisionLedgerAccountCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ProvisionLedgerAccountCommandHandler(ledgerAccountRepository);
    }

    @Test
    void shouldSaveLedgerAccount_whenCommandIsValid() {
        var command = new ProvisionLedgerAccountCommand(ACCOUNT_ID, IBAN, CURRENCY, ACTIVATED_AT);

        handler.handle(command);

        verify(ledgerAccountRepository).save(ledgerAccountCaptor.capture());
        var savedAccount = ledgerAccountCaptor.getValue();

        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getAccountReference().value()).isEqualTo(ACCOUNT_ID);
        assertThat(savedAccount.getAccountType()).isEqualTo(LedgerAccountType.CUSTOMER);
        assertThat(savedAccount.getCurrency().name()).isEqualTo(CURRENCY);
        assertThat(savedAccount.getStatus()).isEqualTo(LedgerAccountStatus.ACTIVE);
        assertThat(savedAccount.getCreatedAt()).isEqualTo(ACTIVATED_AT);
        assertThat(savedAccount.getBalance().booked().amount()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
        assertThat(savedAccount.getBalance().held().amount()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
    }

    @Test
    void shouldSilentlyIgnore_whenAccountAlreadyProvisioned() {
        doThrow(DataIntegrityViolationException.class).when(ledgerAccountRepository).save(any());
        var command = new ProvisionLedgerAccountCommand(ACCOUNT_ID, IBAN, CURRENCY, ACTIVATED_AT);

        handler.handle(command);

        verify(ledgerAccountRepository).save(any());
        verifyNoMoreInteractions(ledgerAccountRepository);
    }
}
