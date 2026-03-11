package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.block;

import com.jcondotta.bankaccounts.application.usecase.block.BlockBankAccountUseCase;
import com.jcondotta.bankaccounts.application.usecase.block.model.BlockBankAccountCommand;
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
class BlockBankAccountControllerImplTest {

  private static final UUID BANK_ACCOUNT_UUID = BankAccountId.newId().value();

  @Mock
  private BlockBankAccountUseCase useCase;

  @Captor
  ArgumentCaptor<BlockBankAccountCommand> commandCaptor;

  private BlockBankAccountControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new BlockBankAccountControllerImpl(useCase);
  }

  @Test
  void shouldBlockBankAccountAndReturnNoContent() {
    ResponseEntity<Void> response = controller.block(BANK_ACCOUNT_UUID);

    verify(useCase).execute(commandCaptor.capture());

    BlockBankAccountCommand command = commandCaptor.getValue();
    assertThat(command.bankAccountId()).isEqualTo(BankAccountId.of(BANK_ACCOUNT_UUID));

    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getBody()).isNull();
  }
}
