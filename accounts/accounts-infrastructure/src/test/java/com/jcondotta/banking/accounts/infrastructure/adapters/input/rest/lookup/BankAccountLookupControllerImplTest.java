package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BankAccountLookupControllerImplTest {

//  private static final UUID BANK_ACCOUNT_UUID = BankAccountId.newId().value();
//
//  @Mock
//  private BankAccountLookupUseCase useCase;
//
//  @Mock
//  private BankAccountLookupResponseControllerMapper mapper;
//
//  @Mock
//  private BankAccountDetails bankAccountDetails;
//
////  @Mock
////  private BankAccountLookupResponse response;
//
//  @Captor
//  ArgumentCaptor<BankAccountId> bankAccountIdCaptor;
//
//  private BankAccountLookupControllerImpl controller;
//
//  @BeforeEach
//  void setUp() {
//    controller = new BankAccountLookupControllerImpl(useCase, mapper);
//  }
//
////  @Test
////  void shouldReturnBankAccountLookupResponse_whenBankAccountExists() {
////    when(useCase.lookup(BankAccountId.of(BANK_ACCOUNT_UUID))).thenReturn(bankAccountDetails);
////    when(mapper.toResponse(bankAccountDetails)).thenReturn(response);
////
////    ResponseEntity<BankAccountLookupResponse> result = controller.getBankAccount(BANK_ACCOUNT_UUID);
////
////    verify(useCase).lookup(bankAccountIdCaptor.capture());
////    assertThat(bankAccountIdCaptor.getValue()).isEqualTo(BankAccountId.of(BANK_ACCOUNT_UUID));
////
////    verify(mapper).toResponse(bankAccountDetails);
////
////    assertThat(result.getStatusCode().value()).isEqualTo(200);
////    assertThat(result.getBody()).isEqualTo(response);
////  }
}
