package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddJointHolderControllerImplTest {

//  private static final UUID BANK_ACCOUNT_UUID = BankAccountId.newId().value();
//
//  private static final String VALID_NAME = AccountHolderFixtures.JEFFERSON.getAccountHolderName().value();
//  private static final String VALID_PASSPORT = AccountHolderFixtures.JEFFERSON.getPassportNumber().value();
//  private static final LocalDate VALID_DATE_OF_BIRTH = AccountHolderFixtures.JEFFERSON.getDateOfBirth().value();
//  private static final String VALID_EMAIL = AccountHolderFixtures.JEFFERSON.getEmail().value();
//
//  @Mock
//  private AddJointAccountHolderUseCase useCase;
//
//  @Mock
//  private AddJointAccountHolderRequestControllerMapper requestMapper;
//
//  @Mock
//  private AddJointAccountHolderCommand command;
//
//  @Captor
//  ArgumentCaptor<AddJointAccountHolderCommand> commandCaptor;
//
//  private AddJointAccountHolderControllerImpl controller;
//
//  @BeforeEach
//  void setUp() {
//    controller = new AddJointAccountHolderControllerImpl(useCase, requestMapper);
//  }
//
//  @Test
//  void shouldCreateJointAccountHolderAndReturnOk() {
//    var restRequest = new AddJointAccountHolderRequest(VALID_NAME, VALID_PASSPORT, VALID_DATE_OF_BIRTH, VALID_EMAIL);
//
//    when(requestMapper.toCommand(BANK_ACCOUNT_UUID, restRequest))
//      .thenReturn(command);
//
//    ResponseEntity<Void> response = controller.createJointAccountHolder(BANK_ACCOUNT_UUID, restRequest);
//
//    verify(requestMapper).toCommand(BANK_ACCOUNT_UUID, restRequest);
//    verify(useCase).execute(commandCaptor.capture());
//
//    assertThat(commandCaptor.getValue()).isEqualTo(command);
//    assertThat(response.getStatusCode().value()).isEqualTo(200);
//    assertThat(response.getBody()).isNull();
//  }
}