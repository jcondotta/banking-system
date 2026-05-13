package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.activate;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.activate.model.ActivateBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActivateBankAccountControllerImplTest {

  private static final UUID BANK_ACCOUNT_UUID = BankAccountId.newId().value();

  @Mock
  private CommandHandler<ActivateBankAccountCommand> commandHandler;

  @Captor
  ArgumentCaptor<ActivateBankAccountCommand> commandCaptor;

  private ActivateBankAccountControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new ActivateBankAccountControllerImpl(commandHandler);
  }

  @Test
  void shouldActivateBankAccountAndReturnNoContent() {
    ResponseEntity<Void> response = controller.activateBankAccount(BANK_ACCOUNT_UUID);

    verify(commandHandler).handle(commandCaptor.capture());

    ActivateBankAccountCommand command = commandCaptor.getValue();
    assertThat(command.bankAccountId()).isEqualTo(BankAccountId.of(BANK_ACCOUNT_UUID));

    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getBody()).isNull();
  }
}
