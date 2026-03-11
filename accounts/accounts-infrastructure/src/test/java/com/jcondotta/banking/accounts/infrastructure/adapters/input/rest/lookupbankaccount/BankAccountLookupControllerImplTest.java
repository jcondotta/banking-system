package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount;

import com.jcondotta.bankaccounts.application.usecase.lookup.BankAccountLookupUseCase;
import com.jcondotta.bankaccounts.application.usecase.lookup.model.BankAccountDetails;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount.mapper.BankAccountLookupResponseControllerMapper;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BankAccountLookupControllerImplTest {

  private static final UUID BANK_ACCOUNT_UUID = BankAccountId.newId().value();

  @Mock
  private BankAccountLookupUseCase useCase;

  @Mock
  private BankAccountLookupResponseControllerMapper mapper;

  @Mock
  private BankAccountDetails bankAccountDetails;

//  @Mock
//  private BankAccountLookupResponse response;

  @Captor
  ArgumentCaptor<BankAccountId> bankAccountIdCaptor;

  private BankAccountLookupControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new BankAccountLookupControllerImpl(useCase, mapper);
  }

//  @Test
//  void shouldReturnBankAccountLookupResponse_whenBankAccountExists() {
//    when(useCase.lookup(BankAccountId.of(BANK_ACCOUNT_UUID))).thenReturn(bankAccountDetails);
//    when(mapper.toResponse(bankAccountDetails)).thenReturn(response);
//
//    ResponseEntity<BankAccountLookupResponse> result = controller.getBankAccount(BANK_ACCOUNT_UUID);
//
//    verify(useCase).lookup(bankAccountIdCaptor.capture());
//    assertThat(bankAccountIdCaptor.getValue()).isEqualTo(BankAccountId.of(BANK_ACCOUNT_UUID));
//
//    verify(mapper).toResponse(bankAccountDetails);
//
//    assertThat(result.getStatusCode().value()).isEqualTo(200);
//    assertThat(result.getBody()).isEqualTo(response);
//  }
}
