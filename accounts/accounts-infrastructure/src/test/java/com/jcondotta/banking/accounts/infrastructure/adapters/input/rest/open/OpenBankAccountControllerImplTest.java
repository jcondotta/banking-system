package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.mapper.OpenBankAccountRequestControllerMapper;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.AccountTypeRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import com.jcondotta.banking.accounts.infrastructure.properties.BankAccountsURIProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenBankAccountControllerImplTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
  private static final URI EXPECTED_LOCATION_URI =
    URI.create("https://api.jcondotta.com/v1/bank-accounts/" + BANK_ACCOUNT_UUID);

  @Mock
  private CommandHandlerWithResult<OpenBankAccountCommand, BankAccountId> commandHandler;

  @Mock
  private OpenBankAccountRequestControllerMapper requestMapper;

  @Mock
  private BankAccountsURIProperties uriProperties;

  @Mock
  private OpenBankAccountRequest request;

  @Mock
  private OpenBankAccountCommand command;

  private OpenBankAccountControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new OpenBankAccountControllerImpl(commandHandler, requestMapper, uriProperties);
  }

  @ParameterizedTest
  @EnumSource(AccountTypeRequest.class)
  void shouldCreateBankAccountAndReturnCreatedResponse_whenRequestIsValid(AccountTypeRequest accountType) {
    when(requestMapper.toCommand(request)).thenReturn(command);
    when(commandHandler.handle(command)).thenReturn(BankAccountId.of(BANK_ACCOUNT_UUID));
    when(uriProperties.bankAccountURI(BANK_ACCOUNT_UUID)).thenReturn(EXPECTED_LOCATION_URI);

    ResponseEntity<Void> response = controller.openBankAccount(request);

    verify(requestMapper).toCommand(request);
    verify(commandHandler).handle(command);
    verify(uriProperties).bankAccountURI(BANK_ACCOUNT_UUID);

    assertThat(response.getStatusCode().value()).isEqualTo(201);
    assertThat(response.getHeaders().getLocation()).isEqualTo(EXPECTED_LOCATION_URI);
    assertThat(response.getBody()).isNull();
  }
}
