package com.jcondotta.banking.ledger.domain.ledger_account.aggregate;

import com.jcondotta.banking.ledger.domain.ledger_account.entries.LedgerEntry;
import com.jcondotta.banking.ledger.domain.ledger_account.enums.LedgerAccountStatus;
import com.jcondotta.banking.ledger.domain.ledger_account.enums.LedgerAccountType;
import com.jcondotta.banking.ledger.domain.ledger_account.value_objects.AccountReference;
import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.movement.Movement;
import com.jcondotta.banking.movement.MovementAmount;
import com.jcondotta.banking.movement.MovementType;
import com.jcondotta.domain.exception.DomainValidationException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LedgerAccountTest {

    private static final AccountReference ACCOUNT_REFERENCE = AccountReference.of(UUID.randomUUID());
    private static final Currency CURRENCY = Currency.EUR;
    private static final Instant ACTIVATED_AT = Instant.parse("2026-05-16T10:15:30Z");
    private static final UUID TRANSFER_ID = UUID.randomUUID();
    private static final MovementAmount MOVEMENT_AMOUNT = MovementAmount.of(new BigDecimal("50.00"), CURRENCY);

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

    @Test
    void shouldReturnDebitEntry_whenMovementIsDebit() {
        var account = LedgerAccount.provision(ACCOUNT_REFERENCE, CURRENCY, ACTIVATED_AT);
        var initialBookedAmount = account.getBalance().booked().amount();

        LedgerEntry entry = account.applyMovement(Movement.ofDebit(MOVEMENT_AMOUNT), TRANSFER_ID, ACTIVATED_AT);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(entry.id()).isNotNull();
            softly.assertThat(entry.accountId()).isEqualTo(account.getId());
            softly.assertThat(entry.transferId()).isEqualTo(TRANSFER_ID);
            softly.assertThat(entry.movementType()).isEqualTo(MovementType.DEBIT);
            softly.assertThat(entry.amount()).isEqualByComparingTo(MOVEMENT_AMOUNT.amount());
            softly.assertThat(entry.currency()).isEqualTo(CURRENCY);
            softly.assertThat(entry.createdAt()).isEqualTo(ACTIVATED_AT);
            softly.assertThat(account.getBalance().booked().amount())
                    .isEqualByComparingTo(initialBookedAmount.subtract(MOVEMENT_AMOUNT.amount()));
        });
    }

    @Test
    void shouldReturnCreditEntry_whenMovementIsCredit() {
        var account = LedgerAccount.provision(ACCOUNT_REFERENCE, CURRENCY, ACTIVATED_AT);
        var initialBookedAmount = account.getBalance().booked().amount();

        LedgerEntry entry = account.applyMovement(Movement.ofCredit(MOVEMENT_AMOUNT), TRANSFER_ID, ACTIVATED_AT);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(entry.id()).isNotNull();
            softly.assertThat(entry.accountId()).isEqualTo(account.getId());
            softly.assertThat(entry.transferId()).isEqualTo(TRANSFER_ID);
            softly.assertThat(entry.movementType()).isEqualTo(MovementType.CREDIT);
            softly.assertThat(entry.amount()).isEqualByComparingTo(MOVEMENT_AMOUNT.amount());
            softly.assertThat(entry.currency()).isEqualTo(CURRENCY);
            softly.assertThat(entry.createdAt()).isEqualTo(ACTIVATED_AT);
            softly.assertThat(account.getBalance().booked().amount())
                    .isEqualByComparingTo(initialBookedAmount.add(MOVEMENT_AMOUNT.amount()));
        });
    }
}
