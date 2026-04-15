package com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipients;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.BankAccountFixtures;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientFixtures;
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
class CreateRecipientCommandHandlerTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Mock
  private BankAccountRepository bankAccountRepository;

  private CreateRecipientCommandHandler commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new CreateRecipientCommandHandler(bankAccountRepository);
  }

  @Test
  void shouldCreateRecipient_whenCommandIsValidAndBankAccountExists() {
    BankAccount bankAccount = BankAccountFixtures.WITH_NO_RECIPIENTS.create();

    when(bankAccountRepository.findById(BANK_ACCOUNT_ID))
      .thenReturn(Optional.of(bankAccount));

    var recipientFixture = RecipientFixtures.JEFFERSON;
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      recipientFixture.toName(),
      recipientFixture.toIban()
    );
    commandHandler.handle(command);

    assertThat(bankAccount.getActiveRecipients())
        .hasSize(1)
        .singleElement()
        .satisfies(
            recipient -> {
              assertThat(recipient.getId()).isNotNull();
              assertThat(recipient.getRecipientName()).isEqualTo(recipientFixture.toName());
              assertThat(recipient.getIban()).isEqualTo(recipientFixture.toIban());
              assertThat(recipient.getCreatedAt()).isNotNull();
            });

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verify(bankAccountRepository).save(bankAccount);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowBankAccountNotFoundException_whenBankAccountDoesNotExist() {
    when(bankAccountRepository.findById(BANK_ACCOUNT_ID)).thenReturn(Optional.empty());

    var recipientFixture = RecipientFixtures.JEFFERSON;
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      recipientFixture.toName(),
      recipientFixture.toIban()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
        .isInstanceOf(BankAccountNotFoundException.class)
        .hasMessage(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND.formatted(BANK_ACCOUNT_ID.value()));

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowDuplicateRecipientException_whenIbanAlreadyExists() {
    var recipientFixture = RecipientFixtures.JEFFERSON;
    var bankAccount = BankAccountFixtures.create(recipientFixture);

    when(bankAccountRepository.findById(BANK_ACCOUNT_ID))
      .thenReturn(Optional.of(bankAccount));

    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      recipientFixture.toName(),
      recipientFixture.toIban()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(DuplicateRecipientIbanException.class)
      .hasMessage(DuplicateRecipientIbanException.RECIPIENT_WITH_IBAN_ALREADY_EXISTS.formatted(recipientFixture.toIban().value()));

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowBankAccountNotActiveException_whenBankAccountIsNotActive() {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, AccountStatus.BLOCKED, Recipients.empty());

    when(bankAccountRepository.findById(BANK_ACCOUNT_ID))
      .thenReturn(Optional.of(bankAccount));

    var recipientFixture = RecipientFixtures.JEFFERSON;
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      recipientFixture.toName(),
      recipientFixture.toIban()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotActiveException.class)
      .hasMessage(new BankAccountNotActiveException(AccountStatus.BLOCKED).getMessage());

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verifyNoMoreInteractions(bankAccountRepository);
  }
}
