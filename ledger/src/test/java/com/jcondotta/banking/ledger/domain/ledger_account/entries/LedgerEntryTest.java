package com.jcondotta.banking.ledger.domain.ledger_account.entries;

import com.jcondotta.banking.ledger.domain.ledger_account.identity.LedgerAccountId;
import com.jcondotta.banking.money.Currency;
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

class LedgerEntryTest {

    private static final LedgerAccountId ACCOUNT_ID = LedgerAccountId.newId();
    private static final UUID TRANSFER_ID = UUID.randomUUID();
    private static final MovementAmount MOVEMENT_AMOUNT = MovementAmount.of(new BigDecimal("100.00"), Currency.EUR);
    private static final Instant CREATED_AT = Instant.parse("2026-05-16T10:15:30Z");

    @Test
    void shouldCreateEntry_whenAllFieldsAreValid() {
        var debitEntry = LedgerEntry.of(ACCOUNT_ID, TRANSFER_ID, MovementType.DEBIT, MOVEMENT_AMOUNT, CREATED_AT);
        var creditEntry = LedgerEntry.of(ACCOUNT_ID, TRANSFER_ID, MovementType.CREDIT, MOVEMENT_AMOUNT, CREATED_AT);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(debitEntry.id()).isNotNull();
            softly.assertThat(debitEntry.accountId()).isEqualTo(ACCOUNT_ID);
            softly.assertThat(debitEntry.transferId()).isEqualTo(TRANSFER_ID);
            softly.assertThat(debitEntry.movementType()).isEqualTo(MovementType.DEBIT);
            softly.assertThat(debitEntry.amount()).isEqualByComparingTo(MOVEMENT_AMOUNT.amount());
            softly.assertThat(debitEntry.currency()).isEqualTo(Currency.EUR);
            softly.assertThat(debitEntry.createdAt()).isEqualTo(CREATED_AT);

            softly.assertThat(creditEntry.id()).isNotNull();
            softly.assertThat(creditEntry.accountId()).isEqualTo(ACCOUNT_ID);
            softly.assertThat(creditEntry.transferId()).isEqualTo(TRANSFER_ID);
            softly.assertThat(creditEntry.movementType()).isEqualTo(MovementType.CREDIT);
            softly.assertThat(creditEntry.amount()).isEqualByComparingTo(MOVEMENT_AMOUNT.amount());
            softly.assertThat(creditEntry.currency()).isEqualTo(Currency.EUR);
            softly.assertThat(creditEntry.createdAt()).isEqualTo(CREATED_AT);
        });
    }

    @Test
    void shouldGenerateUniqueIds_forEachEntry() {
        var entry1 = LedgerEntry.of(ACCOUNT_ID, TRANSFER_ID, MovementType.DEBIT, MOVEMENT_AMOUNT, CREATED_AT);
        var entry2 = LedgerEntry.of(ACCOUNT_ID, TRANSFER_ID, MovementType.CREDIT, MOVEMENT_AMOUNT, CREATED_AT);

        assertThat(entry1.id()).isNotEqualTo(entry2.id());
    }

    @Test
    void shouldThrowException_whenAccountIdIsNull() {
        assertThatThrownBy(() -> LedgerEntry.of(null, TRANSFER_ID, MovementType.DEBIT, MOVEMENT_AMOUNT, CREATED_AT))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldThrowException_whenTransferIdIsNull() {
        assertThatThrownBy(() -> LedgerEntry.of(ACCOUNT_ID, null, MovementType.DEBIT, MOVEMENT_AMOUNT, CREATED_AT))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldThrowException_whenMovementTypeIsNull() {
        assertThatThrownBy(() -> LedgerEntry.of(ACCOUNT_ID, TRANSFER_ID, null, MOVEMENT_AMOUNT, CREATED_AT))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldThrowException_whenMovementAmountIsNull() {
        assertThatThrownBy(() -> LedgerEntry.of(ACCOUNT_ID, TRANSFER_ID, MovementType.DEBIT, null, CREATED_AT))
                .isInstanceOf(DomainValidationException.class);
    }

    @Test
    void shouldThrowException_whenCreatedAtIsNull() {
        assertThatThrownBy(() -> LedgerEntry.of(ACCOUNT_ID, TRANSFER_ID, MovementType.DEBIT, MOVEMENT_AMOUNT, null))
                .isInstanceOf(DomainValidationException.class);
    }
}
