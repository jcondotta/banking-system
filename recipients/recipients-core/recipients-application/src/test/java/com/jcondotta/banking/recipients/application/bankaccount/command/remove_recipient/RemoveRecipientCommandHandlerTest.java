package com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipients;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.BankAccountFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveRecipientCommandHandlerTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Mock
  private BankAccountRepository bankAccountRepository;

  private RemoveRecipientCommandHandler commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new RemoveRecipientCommandHandler(bankAccountRepository);
  }

  @Test
  void shouldRemoveRecipient_whenCommandIsValidAndBankAccountExists() {
    BankAccount bankAccount = BankAccountFixtures.WITH_ONE_RECIPIENT.create();

    when(bankAccountRepository.findById(BANK_ACCOUNT_ID))
      .thenReturn(Optional.of(bankAccount));

    var recipient = bankAccount.getActiveRecipients().getFirst();

    var command = new RemoveRecipientCommand(
      BANK_ACCOUNT_ID,
      recipient.getId()
    );

    commandHandler.handle(command);

    assertThat(bankAccount.getActiveRecipients()).isEmpty();
    assertThat(bankAccount.getRecipients())
      .singleElement()
      .satisfies(removedRecipient -> assertThat(removedRecipient.getStatus()).isEqualTo(RecipientStatus.REMOVED));

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verify(bankAccountRepository).save(bankAccount);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldBeIdempotent_whenRemovingRecipientTwice() {
    BankAccount bankAccount = BankAccountFixtures.WITH_ONE_RECIPIENT.create();
    var recipient = bankAccount.getActiveRecipients().getFirst();

    when(bankAccountRepository.findById(BANK_ACCOUNT_ID))
      .thenReturn(Optional.of(bankAccount));

    var command = new RemoveRecipientCommand(
      BANK_ACCOUNT_ID,
      recipient.getId()
    );

    assertThatCode(() -> {
      commandHandler.handle(command);
      commandHandler.handle(command);
    }).doesNotThrowAnyException();

    assertThat(bankAccount.getActiveRecipients()).isEmpty();
    assertThat(bankAccount.getRecipients())
      .singleElement()
      .satisfies(removedRecipient -> assertThat(removedRecipient.getStatus()).isEqualTo(RecipientStatus.REMOVED));

    verify(bankAccountRepository, times(2)).findById(BANK_ACCOUNT_ID);
    verify(bankAccountRepository, times(2)).save(bankAccount);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowBankAccountNotFoundException_whenBankAccountDoesNotExist() {
    when(bankAccountRepository.findById(BANK_ACCOUNT_ID))
      .thenReturn(Optional.empty());

    var command = new RemoveRecipientCommand(
      BANK_ACCOUNT_ID,
      RecipientId.newId()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotFoundException.class)
      .hasMessage(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND.formatted(BANK_ACCOUNT_ID.value()));

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowBankAccountNotActiveException_whenBankAccountIsNotActive() {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.BLOCKED, Recipients.empty());

    when(bankAccountRepository.findById(BANK_ACCOUNT_ID))
      .thenReturn(Optional.of(bankAccount));

    var command = new RemoveRecipientCommand(
      BANK_ACCOUNT_ID,
      RecipientId.newId()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotActiveException.class)
      .hasMessage(new BankAccountNotActiveException(AccountStatus.BLOCKED).getMessage());

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verify(bankAccountRepository, never()).save(any(BankAccount.class));
    verifyNoMoreInteractions(bankAccountRepository);
  }
}
