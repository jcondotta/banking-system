package com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.transfers.application.bank_account.ports.output.BankAccountLookupPort;
import com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal.model.RequestInternalTransferCommand;
import com.jcondotta.banking.transfers.domain.bank_account.BankAccountSummary;
import com.jcondotta.banking.transfers.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotActiveException;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotFoundException;
import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_account.value_objects.Iban;
import com.jcondotta.banking.transfers.domain.bank_transfer.aggregate.BankTransfer;
import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferStatus;
import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferType;
import com.jcondotta.banking.transfers.domain.bank_transfer.exceptions.IdenticalInternalPartiesException;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.repository.BankTransferRepository;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.PartyName;
import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.domain.movement.MovementAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestInternalTransferCommandHandlerTest {

  private static final BankAccountId SENDER_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final BankAccountId RECIPIENT_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final BankAccountSummary ACTIVE_RECIPIENT_SUMMARY = new BankAccountSummary(RECIPIENT_ACCOUNT_ID, BankAccountStatus.ACTIVE);
  private static final PartyName RECIPIENT_NAME = PartyName.of("Jane Recipient");
  private static final Iban RECIPIENT_IBAN = Iban.of("ES9121000418450200051332");
  private static final MovementAmount MONETARY_AMOUNT = MovementAmount.of(new BigDecimal("100.00"), Currency.EUR);
  private static final String REFERENCE = "invoice #123";
  private static final Instant REQUESTED_AT = Instant.parse("2026-05-16T10:15:30Z");
  private static final Clock FIXED_CLOCK = Clock.fixed(REQUESTED_AT, ZoneOffset.UTC);

  @Mock
  private BankTransferRepository bankTransferRepository;

  @Mock
  private BankAccountLookupPort bankAccountLookupPort;

  @Captor
  private ArgumentCaptor<BankTransfer> bankTransferCaptor;

  private CommandHandlerWithResult<RequestInternalTransferCommand, BankTransferId> commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new RequestInternalTransferCommandHandler(bankTransferRepository, bankAccountLookupPort, FIXED_CLOCK);
  }

  @Test
  void shouldRequestInternalTransfer_whenCommandIsValid() {
    when(bankAccountLookupPort.findByIban(RECIPIENT_IBAN)).thenReturn(Optional.of(ACTIVE_RECIPIENT_SUMMARY));

    var command = command();

    var bankTransferId = commandHandler.handle(command);

    verify(bankAccountLookupPort).findByIban(RECIPIENT_IBAN);
    verify(bankTransferRepository).save(bankTransferCaptor.capture());
    verifyNoMoreInteractions(bankAccountLookupPort, bankTransferRepository);

    assertThat(bankTransferId).isEqualTo(bankTransferCaptor.getValue().getId());

    assertThat(bankTransferCaptor.getValue())
      .satisfies(bankTransfer -> {
        assertThat(bankTransfer.getId()).isNotNull();
        assertThat(bankTransfer.getTransferType()).isEqualTo(TransferType.INTERNAL);
        assertThat(bankTransfer.getTransferStatus()).isEqualTo(TransferStatus.PENDING);
        assertThat(bankTransfer.getReference()).isEqualTo(REFERENCE);
        assertThat(bankTransfer.getCreatedAt()).isEqualTo(REQUESTED_AT);
        assertThat(bankTransfer.getTransferEntries()).hasSize(2);
      });
  }

  @Test
  void shouldThrowDomainException_whenCommandBreaksDomainRule() {
    when(bankAccountLookupPort.findByIban(RECIPIENT_IBAN)).thenReturn(Optional.of(new BankAccountSummary(SENDER_ACCOUNT_ID, BankAccountStatus.ACTIVE)));

    var command = new RequestInternalTransferCommand(
      SENDER_ACCOUNT_ID,
      RECIPIENT_NAME,
      RECIPIENT_IBAN,
      MONETARY_AMOUNT,
      REFERENCE
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(IdenticalInternalPartiesException.class)
      .hasMessage(IdenticalInternalPartiesException.MESSAGE);

    verify(bankAccountLookupPort).findByIban(RECIPIENT_IBAN);
    verifyNoInteractions(bankTransferRepository);
    verifyNoMoreInteractions(bankAccountLookupPort);
  }

  @Test
  void shouldThrowDomainException_whenRecipientIbanDoesNotResolveToInternalAccount() {
    when(bankAccountLookupPort.findByIban(RECIPIENT_IBAN)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> commandHandler.handle(command()))
      .isInstanceOf(RecipientBankAccountNotFoundException.class)
      .hasMessage(RecipientBankAccountNotFoundException.MESSAGE);

    verify(bankAccountLookupPort).findByIban(RECIPIENT_IBAN);
    verifyNoInteractions(bankTransferRepository);
    verifyNoMoreInteractions(bankAccountLookupPort);
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    when(bankAccountLookupPort.findByIban(RECIPIENT_IBAN)).thenReturn(Optional.of(ACTIVE_RECIPIENT_SUMMARY));

    var exception = new IllegalStateException("database unavailable");

    doThrow(exception)
      .when(bankTransferRepository)
      .save(any(BankTransfer.class));

    assertThatThrownBy(() -> commandHandler.handle(command()))
      .isSameAs(exception);

    verify(bankAccountLookupPort).findByIban(RECIPIENT_IBAN);
    verify(bankTransferRepository).save(any(BankTransfer.class));
    verifyNoMoreInteractions(bankAccountLookupPort, bankTransferRepository);
  }

  @ParameterizedTest
  @EnumSource(value = BankAccountStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
  void shouldThrowException_whenRecipientBankAccountIsNotActive(BankAccountStatus status) {
    when(bankAccountLookupPort.findByIban(RECIPIENT_IBAN))
      .thenReturn(Optional.of(new BankAccountSummary(RECIPIENT_ACCOUNT_ID, status)));

    assertThatThrownBy(() -> commandHandler.handle(command()))
      .isInstanceOf(RecipientBankAccountNotActiveException.class)
      .hasMessage(RecipientBankAccountNotActiveException.MESSAGE);

    verify(bankAccountLookupPort).findByIban(RECIPIENT_IBAN);
    verifyNoInteractions(bankTransferRepository);
    verifyNoMoreInteractions(bankAccountLookupPort);
  }

  private static RequestInternalTransferCommand command() {
    return new RequestInternalTransferCommand(
      SENDER_ACCOUNT_ID,
      RECIPIENT_NAME,
      RECIPIENT_IBAN,
      MONETARY_AMOUNT,
      REFERENCE
    );
  }
}
