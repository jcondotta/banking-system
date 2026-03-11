package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenBankAccountControllerImplTest {

//    private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
//
//    private static final String VALID_NAME = AccountHolderFixtures.JEFFERSON.getAccountHolderName().value();
//    private static final String VALID_PASSPORT = AccountHolderFixtures.JEFFERSON.getPassportNumber().value();
//    private static final LocalDate VALID_DATE_OF_BIRTH = AccountHolderFixtures.JEFFERSON.getDateOfBirth().value();
//    private static final String VALID_EMAIL = AccountHolderFixtures.JEFFERSON.getEmail().value();
//
//    private static final Clock FIXED_CLOCK = ClockTestFactory.FIXED_CLOCK;
//
//    private static final URI EXPECTED_LOCATION_URI =
//            URI.create("https://api.jcondotta.com/v1/bank-accounts/" + BANK_ACCOUNT_UUID);
//
//    @Mock
//    private OpenBankAccountUseCase useCase;
//
//    private OpenBankAccountRequestControllerMapper requestMapper = Mappers.getMapper(OpenBankAccountRequestControllerMapper.class);
//
//    @Mock
//    private BankAccountsURIProperties uriProperties;
//
//    private OpenBankAccountControllerImpl controller;
//
//    @BeforeEach
//    void setUp() {
//        controller =
//                new OpenBankAccountControllerImpl(useCase, requestMapper, uriProperties);
//    }
//
//    @ParameterizedTest
//    @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
//    void shouldCreateAccountRecipientAndReturnCreatedResponse_whenRequestIsValid(AccountType accountType, Currency currency) {
//        var primaryAccountHolderRequest = new PrimaryAccountHolderRequest(VALID_NAME, VALID_PASSPORT, VALID_DATE_OF_BIRTH, VALID_EMAIL);
//        var request = new OpenBankAccountRequest(accountType, currency, primaryAccountHolderRequest);
//
//        var openBankAccountCommand = requestMapper.toCommand(request);
//
//        when(useCase.execute(openBankAccountCommand))
//          .thenReturn(new OpenBankAccountResult(BankAccountId.of(BANK_ACCOUNT_UUID), Instant.now(FIXED_CLOCK)));
//
//        when(uriProperties.bankAccountURI(BANK_ACCOUNT_UUID)).thenReturn(EXPECTED_LOCATION_URI);
//
//        ResponseEntity<Void> response = controller.openBankAccount(request);
//
//        assertThat(response.getStatusCode().value()).isEqualTo(201);
//        assertThat(response.getHeaders().getLocation()).isEqualTo(EXPECTED_LOCATION_URI);
//        assertThat(response.getBody()).isNull();
//
//        verify(useCase).execute(openBankAccountCommand);
//        verify(uriProperties).bankAccountURI(BANK_ACCOUNT_UUID);
//
//        verifyNoMoreInteractions(useCase, uriProperties);
//    }
}