package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.exceptions.IdenticalInternalPartiesException;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.InternalAccountRecipient;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.InternalAccountSender;
import com.jcondotta.banking.transfers.domain.monetary_movement.enums.MovementType;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryMovement;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;
import com.jcondotta.banking.transfers.domain.testsupport.MovementTypeAndCurrencyArgumentsProvider;
import com.jcondotta.domain.exception.InvalidDomainDataException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalTransferEntryTest {

    private static final BankAccountId SENDER_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final BankAccountId RECIPIENT_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

    private static final BigDecimal AMOUNT_200 = new BigDecimal("200.00");

    @ParameterizedTest
    @ArgumentsSource(MovementTypeAndCurrencyArgumentsProvider.class)
    void shouldCreateInternalTransferEntry_whenParamsAreValid(MovementType movementType, Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);
        var monetaryMovement = MonetaryMovement.of(movementType, monetaryAmount);
        var sender = InternalAccountSender.of(SENDER_ACCOUNT_ID);
        var recipient = InternalAccountRecipient.of(RECIPIENT_ACCOUNT_ID);

        var entry = new InternalTransferEntry(sender, recipient, monetaryMovement);

        assertThat(entry.partySender().bankAccountId()).isEqualTo(SENDER_ACCOUNT_ID);
        assertThat(entry.partyRecipient().bankAccountId()).isEqualTo(RECIPIENT_ACCOUNT_ID);
        assertThat(entry.monetaryMovement()).isEqualTo(monetaryMovement);
        assertThat(entry.amount()).isEqualTo(AMOUNT_200);
        assertThat(entry.currency()).isEqualTo(currency);
        assertThat(entry.movementType()).isEqualTo(movementType);
        assertThat(entry.isDebit()).isEqualTo(movementType.isDebit());
        assertThat(entry.isCredit()).isEqualTo(movementType.isCredit());
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateInternalTransferEntry_whenUsingOfDebitFactory(Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);
        var entry = InternalTransferEntry.ofDebit(SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, monetaryAmount);

        assertThat(entry.partySender().bankAccountId()).isEqualTo(SENDER_ACCOUNT_ID);
        assertThat(entry.partyRecipient().bankAccountId()).isEqualTo(RECIPIENT_ACCOUNT_ID);
        assertThat(entry.movementType()).isEqualTo(MovementType.DEBIT);
        assertThat(entry.isDebit()).isTrue();
        assertThat(entry.isCredit()).isFalse();
        assertThat(entry.amount()).isEqualTo(AMOUNT_200);
        assertThat(entry.currency()).isEqualTo(currency);
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldCreateInternalTransferEntry_whenUsingOfCreditFactory(Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);
        var entry = InternalTransferEntry.ofCredit(SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, monetaryAmount);

        assertThat(entry.partySender().bankAccountId()).isEqualTo(SENDER_ACCOUNT_ID);
        assertThat(entry.partyRecipient().bankAccountId()).isEqualTo(RECIPIENT_ACCOUNT_ID);
        assertThat(entry.movementType()).isEqualTo(MovementType.CREDIT);
        assertThat(entry.isCredit()).isTrue();
        assertThat(entry.isDebit()).isFalse();
        assertThat(entry.amount()).isEqualTo(AMOUNT_200);
        assertThat(entry.currency()).isEqualTo(currency);
    }

    @ParameterizedTest
    @ArgumentsSource(MovementTypeAndCurrencyArgumentsProvider.class)
    void shouldThrowException_whenPartiesAreIdentical(MovementType movementType, Currency currency) {
        var sameAccountId = BankAccountId.of(UUID.randomUUID());
        var monetaryMovement = MonetaryMovement.of(movementType, MonetaryAmount.of(AMOUNT_200, currency));
        var sender = InternalAccountSender.of(sameAccountId);
        var recipient = InternalAccountRecipient.of(sameAccountId);

        assertThatThrownBy(() -> new InternalTransferEntry(sender, recipient, monetaryMovement))
            .isInstanceOf(IdenticalInternalPartiesException.class)
            .hasMessage(IdenticalInternalPartiesException.MESSAGE);
    }

    @ParameterizedTest
    @ArgumentsSource(MovementTypeAndCurrencyArgumentsProvider.class)
    void shouldThrowException_whenSenderIsNull(MovementType movementType, Currency currency) {
        var monetaryMovement = MonetaryMovement.of(movementType, MonetaryAmount.of(AMOUNT_200, currency));
        var recipient = InternalAccountRecipient.of(RECIPIENT_ACCOUNT_ID);

        assertThatThrownBy(() -> new InternalTransferEntry(null, recipient, monetaryMovement))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(InternalAccountSender.SENDER_ACCOUNT_ID_NOT_PROVIDED);
    }

    @ParameterizedTest
    @ArgumentsSource(MovementTypeAndCurrencyArgumentsProvider.class)
    void shouldThrowException_whenRecipientIsNull(MovementType movementType, Currency currency) {
        var monetaryMovement = MonetaryMovement.of(movementType, MonetaryAmount.of(AMOUNT_200, currency));
        var sender = InternalAccountSender.of(SENDER_ACCOUNT_ID);

        assertThatThrownBy(() -> new InternalTransferEntry(sender, null, monetaryMovement))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(InternalAccountRecipient.RECIPIENT_ACCOUNT_ID_NOT_PROVIDED);
    }

    @Test
    void shouldThrowException_whenMonetaryMovementIsNull() {
        var sender = InternalAccountSender.of(SENDER_ACCOUNT_ID);
        var recipient = InternalAccountRecipient.of(RECIPIENT_ACCOUNT_ID);

        assertThatThrownBy(() -> new InternalTransferEntry(sender, recipient, null))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(TransferEntry.MONETARY_MOVEMENT_NOT_PROVIDED);
    }

    @ParameterizedTest
    @ArgumentsSource(MovementTypeAndCurrencyArgumentsProvider.class)
    void shouldThrowException_whenUsingOfFactoryWithNullSenderAccountId(MovementType movementType, Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);

        assertThatThrownBy(() -> InternalTransferEntry.of(null, RECIPIENT_ACCOUNT_ID, movementType, monetaryAmount))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(InternalAccountSender.SENDER_ACCOUNT_ID_NOT_PROVIDED);
    }

    @ParameterizedTest
    @ArgumentsSource(MovementTypeAndCurrencyArgumentsProvider.class)
    void shouldThrowException_whenUsingOfFactoryWithNullRecipientAccountId(MovementType movementType, Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);

        assertThatThrownBy(() -> InternalTransferEntry.of(SENDER_ACCOUNT_ID, null, movementType, monetaryAmount))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(InternalAccountRecipient.RECIPIENT_ACCOUNT_ID_NOT_PROVIDED);
    }

    @ParameterizedTest
    @EnumSource(Currency.class)
    void shouldThrowException_whenUsingOfFactoryWithNullMovementType(Currency currency) {
        var monetaryAmount = MonetaryAmount.of(AMOUNT_200, currency);

        assertThatThrownBy(() -> InternalTransferEntry.of(SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, null, monetaryAmount))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(MonetaryMovement.MOVEMENT_TYPE_NOT_PROVIDED);
    }

    @ParameterizedTest
    @EnumSource(MovementType.class)
    void shouldThrowException_whenUsingOfFactoryWithNullMonetaryAmount(MovementType movementType) {
        assertThatThrownBy(() -> InternalTransferEntry.of(SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, movementType, null))
            .isInstanceOf(InvalidDomainDataException.class)
            .hasMessage(MonetaryMovement.MONETARY_AMOUNT_NOT_PROVIDED);
    }
}
