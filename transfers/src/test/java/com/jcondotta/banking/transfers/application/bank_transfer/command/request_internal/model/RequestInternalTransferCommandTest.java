package com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal.model;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_account.value_objects.Iban;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.PartyName;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestInternalTransferCommandTest {

  private static final BankAccountId SENDER_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final PartyName RECIPIENT_NAME = PartyName.of("Jane Recipient");
  private static final Iban RECIPIENT_IBAN = Iban.of("ES9121000418450200051332");
  private static final MonetaryAmount MONETARY_AMOUNT = MonetaryAmount.of(new BigDecimal("100.00"), Currency.EUR);
  private static final String REFERENCE = "invoice #123";

  @Test
  void shouldCreateCommand_whenValuesAreValid() {
    var command = new RequestInternalTransferCommand(
      SENDER_ACCOUNT_ID,
      RECIPIENT_NAME,
      RECIPIENT_IBAN,
      MONETARY_AMOUNT,
      REFERENCE
    );

    assertThat(command.senderAccountId()).isEqualTo(SENDER_ACCOUNT_ID);
    assertThat(command.recipientName()).isEqualTo(RECIPIENT_NAME);
    assertThat(command.recipientIban()).isEqualTo(RECIPIENT_IBAN);
    assertThat(command.monetaryAmount()).isEqualTo(MONETARY_AMOUNT);
    assertThat(command.reference()).isEqualTo(REFERENCE);
  }

  @Test
  void shouldCreateCommand_whenReferenceIsNull() {
    var command = new RequestInternalTransferCommand(
      SENDER_ACCOUNT_ID,
      RECIPIENT_NAME,
      RECIPIENT_IBAN,
      MONETARY_AMOUNT,
      null
    );

    assertThat(command.reference()).isNull();
  }

  @Test
  void shouldThrowException_whenSenderAccountIdIsNull() {
    assertThatThrownBy(() -> new RequestInternalTransferCommand(null, RECIPIENT_NAME, RECIPIENT_IBAN, MONETARY_AMOUNT, REFERENCE))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(RequestInternalTransferCommand.SENDER_ACCOUNT_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenRecipientNameIsNull() {
    assertThatThrownBy(() -> new RequestInternalTransferCommand(SENDER_ACCOUNT_ID, null, RECIPIENT_IBAN, MONETARY_AMOUNT, REFERENCE))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(RequestInternalTransferCommand.RECIPIENT_NAME_REQUIRED);
  }

  @Test
  void shouldThrowException_whenRecipientIbanIsNull() {
    assertThatThrownBy(() -> new RequestInternalTransferCommand(SENDER_ACCOUNT_ID, RECIPIENT_NAME, null, MONETARY_AMOUNT, REFERENCE))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(RequestInternalTransferCommand.RECIPIENT_IBAN_REQUIRED);
  }

  @Test
  void shouldThrowException_whenMonetaryAmountIsNull() {
    assertThatThrownBy(() -> new RequestInternalTransferCommand(SENDER_ACCOUNT_ID, RECIPIENT_NAME, RECIPIENT_IBAN, null, REFERENCE))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(RequestInternalTransferCommand.MONETARY_AMOUNT_REQUIRED);
  }
}
