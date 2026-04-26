package com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class CreateRecipientCommandHandlerTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  private static final RecipientName RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName();
  private static final Iban IBAN = RecipientFixtures.JEFFERSON.toIban();

  private static final Clock CLOCK = ClockTestFactory.FIXED_CLOCK;

  @Mock
  private RecipientRepository recipientRepository;

  @Captor
  private ArgumentCaptor<Recipient> recipientCaptor;

  private CreateRecipientCommandHandler commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new CreateRecipientCommandHandler(recipientRepository, CLOCK);
  }

  @Test
  void shouldCreateRecipient_whenCommandIsValid() {
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN
    );

    var recipientId = commandHandler.handle(command);

    verify(recipientRepository).create(recipientCaptor.capture());
    var savedRecipient = recipientCaptor.getValue();

    assertThat(savedRecipient.getId()).isEqualTo(recipientId);
    assertThat(savedRecipient.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(savedRecipient.getRecipientName()).isEqualTo(RECIPIENT_NAME);
    assertThat(savedRecipient.getIban()).isEqualTo(IBAN);
    assertThat(savedRecipient.getCreatedAt()).isEqualTo(CLOCK.instant());
    assertThat(savedRecipient.getVersion()).isNull();
  }

  @Test
  void shouldCallRepositoryCreate_whenCommandIsValid() {
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN
    );

    commandHandler.handle(command);

    verify(recipientRepository).create(recipientCaptor.capture());
    verifyNoMoreInteractions(recipientRepository);
  }

  @Test
  void shouldReturnRecipientId_whenRecipientIsCreated() {
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN
    );

    var recipientId = commandHandler.handle(command);

    verify(recipientRepository).create(recipientCaptor.capture());
    assertThat(recipientId).isEqualTo(recipientCaptor.getValue().getId());
  }
}
