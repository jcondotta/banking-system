package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.validation.BankTransferErrors;
import com.jcondotta.domain.exception.DomainValidationException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InternalTransferRequestedDataTest {

    private static final BankAccountId SENDER_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final BankAccountId RECIPIENT_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
    private static final BigDecimal AMOUNT = new BigDecimal("200.00");
    private static final Currency CURRENCY = Currency.USD;
    private static final String REFERENCE = "payment for invoice #123";

    @Test
    void shouldCreateData_whenAllParamsAreValid() {
        var data = new InternalTransferRequestedData(
            SENDER_ACCOUNT_ID,
            RECIPIENT_ACCOUNT_ID,
            AMOUNT,
            CURRENCY,
            REFERENCE
        );

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(data.senderAccountId()).isEqualTo(SENDER_ACCOUNT_ID);
            softly.assertThat(data.recipientAccountId()).isEqualTo(RECIPIENT_ACCOUNT_ID);
            softly.assertThat(data.amount()).isEqualByComparingTo(AMOUNT);
            softly.assertThat(data.currency()).isEqualTo(CURRENCY);
            softly.assertThat(data.reference()).isEqualTo(REFERENCE);
        });
    }

    @Test
    void shouldCreateData_whenReferenceIsNull() {
        var data = new InternalTransferRequestedData(
            SENDER_ACCOUNT_ID,
            RECIPIENT_ACCOUNT_ID,
            AMOUNT,
            CURRENCY,
            null
        );

        assertThat(data.reference()).isNull();
    }

    @Test
    void shouldThrowException_whenSenderAccountIdIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedData(null, RECIPIENT_ACCOUNT_ID, AMOUNT, CURRENCY, REFERENCE)
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(BankTransferErrors.SENDER_ACCOUNT_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenRecipientAccountIdIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedData(SENDER_ACCOUNT_ID, null, AMOUNT, CURRENCY, REFERENCE)
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(BankTransferErrors.RECIPIENT_ACCOUNT_ID_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenAmountIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedData(SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, null, CURRENCY, REFERENCE)
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(BankTransferErrors.AMOUNT_MUST_BE_PROVIDED);
    }

    @Test
    void shouldThrowException_whenCurrencyIsNull() {
        assertThatThrownBy(() ->
            new InternalTransferRequestedData(SENDER_ACCOUNT_ID, RECIPIENT_ACCOUNT_ID, AMOUNT, null, REFERENCE)
        )
            .isInstanceOf(DomainValidationException.class)
            .hasMessage(BankTransferErrors.CURRENCY_MUST_BE_PROVIDED);
    }
}
