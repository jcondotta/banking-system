package com.jcondotta.banking.transfers.ledger.domain.ledger_account.aggregate;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.enums.LedgerAccountStatus;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.enums.LedgerAccountType;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.AccountReference;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LedgerAccountTest {

    private static final AccountReference ACCOUNT_REFERENCE = AccountReference.of(UUID.randomUUID());
    private static final Currency CURRENCY = Currency.EUR;
    private static final Instant ACTIVATED_AT = Instant.parse("2026-05-16T10:15:30Z");

    @Test
    void shouldProvisionCustomerAccount_whenParamsAreValid() {
        var account = LedgerAccount.provision(ACCOUNT_REFERENCE, CURRENCY, ACTIVATED_AT);

        assertThat(account.getId()).isNotNull();
        assertThat(account.getAccountReference()).isEqualTo(ACCOUNT_REFERENCE);
        assertThat(account.getAccountType()).isEqualTo(LedgerAccountType.CUSTOMER);
        assertThat(account.getCurrency()).isEqualTo(CURRENCY);
        assertThat(account.getStatus()).isEqualTo(LedgerAccountStatus.ACTIVE);
        assertThat(account.getCreatedAt()).isEqualTo(ACTIVATED_AT);
        assertThat(account.getBalance().booked().amount()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
        assertThat(account.getBalance().held().amount()).isEqualByComparingTo(java.math.BigDecimal.ZERO);
        assertThat(account.getBalance().booked().currency()).isEqualTo(CURRENCY);
        assertThat(account.getBalance().held().currency()).isEqualTo(CURRENCY);
    }

    @Test
    void shouldGenerateUniqueIds_forEachProvisionedAccount() {
        var account1 = LedgerAccount.provision(ACCOUNT_REFERENCE, CURRENCY, ACTIVATED_AT);
        var account2 = LedgerAccount.provision(AccountReference.of(UUID.randomUUID()), CURRENCY, ACTIVATED_AT);

        assertThat(account1.getId()).isNotEqualTo(account2.getId());
    }

    @Test
    void shouldThrowException_whenAccountReferenceIsNull() {
        assertThatThrownBy(() -> LedgerAccount.provision(null, CURRENCY, ACTIVATED_AT))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldThrowException_whenCurrencyIsNull() {
        assertThatThrownBy(() -> LedgerAccount.provision(ACCOUNT_REFERENCE, null, ACTIVATED_AT))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldThrowException_whenCreatedAtIsNull() {
        assertThatThrownBy(() -> LedgerAccount.provision(ACCOUNT_REFERENCE, CURRENCY, null))
                .isInstanceOf(DomainValidationException.class);
    }
}
