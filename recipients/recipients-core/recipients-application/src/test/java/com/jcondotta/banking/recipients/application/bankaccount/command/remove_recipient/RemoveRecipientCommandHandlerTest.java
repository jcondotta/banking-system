package com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOwnershipMismatchException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveRecipientCommandHandlerTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  private static final RecipientName RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName();
  private static final Iban IBAN = RecipientFixtures.JEFFERSON.toIban();

  private static final Instant CREATED_AT = ClockTestFactory.FIXED_CLOCK.instant();

  @Mock
  private RecipientRepository recipientRepository;

  private RemoveRecipientCommandHandler commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new RemoveRecipientCommandHandler(recipientRepository);
  }

  @Test
  void shouldRemoveRecipient_whenRecipientExists() {
    var recipient = recipient();
    var command = new RemoveRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(recipientRepository.findById(RECIPIENT_ID))
      .thenReturn(Optional.of(recipient));

    commandHandler.handle(command);

    verify(recipientRepository).findById(RECIPIENT_ID);
    verify(recipientRepository).delete(recipient);
    verifyNoMoreInteractions(recipientRepository);
  }

  @Test
  void shouldThrowRecipientOwnershipMismatchException_whenOwnershipMismatch() {
    var recipient = recipient();
    var anotherBankAccountId = BankAccountId.of(UUID.randomUUID());
    var command = new RemoveRecipientCommand(anotherBankAccountId, RECIPIENT_ID);

    when(recipientRepository.findById(RECIPIENT_ID))
      .thenReturn(Optional.of(recipient));

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(RecipientOwnershipMismatchException.class);

    verify(recipientRepository).findById(RECIPIENT_ID);
    verify(recipientRepository, never()).delete(recipient);
    verifyNoMoreInteractions(recipientRepository);
  }

  @Test
  void shouldThrowRecipientNotFoundException_whenRecipientDoesNotExist() {
    var command = new RemoveRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(recipientRepository.findById(RECIPIENT_ID))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(RecipientNotFoundException.class);

    verify(recipientRepository).findById(RECIPIENT_ID);
    verifyNoMoreInteractions(recipientRepository);
  }

  private static Recipient recipient() {
    return Recipient.restore(
      RECIPIENT_ID,
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN,
      CREATED_AT,
      0L
    );
  }
}
