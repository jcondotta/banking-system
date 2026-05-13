package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.GetBankAccountByIdQuery;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.mapper.BankAccountLookupResponseControllerMapper;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.BankAccountDetailsResponse;
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
class BankAccountLookupControllerImplTest {

  private static final UUID BANK_ACCOUNT_UUID = BankAccountId.newId().value();

  @Mock
  private QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> queryHandler;

  @Mock
  private BankAccountLookupResponseControllerMapper mapper;

  @Mock
  private BankAccountSummary bankAccountSummary;

  @Mock
  private BankAccountDetailsResponse bankAccountDetailsResponse;

  @Captor
  ArgumentCaptor<GetBankAccountByIdQuery> queryCaptor;

  private BankAccountLookupControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new BankAccountLookupControllerImpl(queryHandler, mapper);
  }

  @Test
  void shouldReturnBankAccountDetailsResponse_whenBankAccountExists() {
    when(queryHandler.handle(new GetBankAccountByIdQuery(new BankAccountId(BANK_ACCOUNT_UUID))))
      .thenReturn(bankAccountSummary);
    when(mapper.toBankAccountDetailsResponse(bankAccountSummary)).thenReturn(bankAccountDetailsResponse);

    ResponseEntity<BankAccountDetailsResponse> result = controller.getBankAccount(BANK_ACCOUNT_UUID);

    verify(queryHandler).handle(queryCaptor.capture());
    assertThat(queryCaptor.getValue().bankAccountId()).isEqualTo(new BankAccountId(BANK_ACCOUNT_UUID));

    verify(mapper).toBankAccountDetailsResponse(bankAccountSummary);

    assertThat(result.getStatusCode().value()).isEqualTo(200);
    assertThat(result.getBody()).isEqualTo(bankAccountDetailsResponse);
  }
}
