package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.mapper.AddJointHolderRequestControllerMapper;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddJointHolderControllerImplTest {

  private static final UUID BANK_ACCOUNT_UUID = BankAccountId.newId().value();

  @Mock
  private CommandHandler<AddJointHolderCommand> commandHandler;

  @Mock
  private AddJointHolderRequestControllerMapper requestMapper;

  @Mock
  private AddJointHolderRequest restRequest;

  @Mock
  private AddJointHolderCommand command;

  @Captor
  ArgumentCaptor<AddJointHolderCommand> commandCaptor;

  private AddJointHolderControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new AddJointHolderControllerImpl(commandHandler, requestMapper);
  }

  @Test
  void shouldCreateJointAccountHolderAndReturnNoContent() {
    when(requestMapper.toCommand(BANK_ACCOUNT_UUID, restRequest)).thenReturn(command);

    ResponseEntity<Void> response = controller.createJointAccountHolder(BANK_ACCOUNT_UUID, restRequest);

    verify(requestMapper).toCommand(BANK_ACCOUNT_UUID, restRequest);
    verify(commandHandler).handle(commandCaptor.capture());

    assertThat(commandCaptor.getValue()).isEqualTo(command);
    assertThat(response.getStatusCode().value()).isEqualTo(204);
    assertThat(response.getBody()).isNull();
  }
}
