package com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.BankAccountFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    var recipient = bankAccount.getRecipients().getFirst();

    var command = new RemoveRecipientCommand(
      BANK_ACCOUNT_ID,
      recipient.getId()
    );

    commandHandler.handle(command);

    assertThat(bankAccount.getActiveRecipients()).isEmpty();

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verify(bankAccountRepository).save(bankAccount);
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
}