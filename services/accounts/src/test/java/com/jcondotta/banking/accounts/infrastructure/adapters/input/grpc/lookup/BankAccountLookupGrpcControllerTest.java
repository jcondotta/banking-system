package com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.lookup;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.GetBankAccountByIbanQuery;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.GetBankAccountByIdQuery;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.BankAccount;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.BankAccountDetailsGrpcResponse;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.FindBankAccountByIbanRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.FindBankAccountByIdRequest;
import io.grpc.stub.StreamObserver;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankAccountLookupGrpcControllerTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("01920bff-1338-7efd-ade6-e9128debe5d4");
  private static final String IBAN = "ES3801283316232166447417";

  @Mock
  private QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> getByIdQueryHandler;
  @Mock
  private QueryHandler<GetBankAccountByIbanQuery, BankAccountSummary> getByIbanQueryHandler;
  @Mock
  private BankAccountGrpcMapper mapper;
  @Mock
  private BankAccountSummary summary;
  @Mock
  private StreamObserver<BankAccountDetailsGrpcResponse> findByIdResponseObserver;
  @Mock
  private StreamObserver<BankAccountDetailsGrpcResponse> findByIbanResponseObserver;
  @Captor
  private ArgumentCaptor<GetBankAccountByIdQuery> idQueryCaptor;
  @Captor
  private ArgumentCaptor<GetBankAccountByIbanQuery> ibanQueryCaptor;
  @Captor
  private ArgumentCaptor<BankAccountDetailsGrpcResponse> findByIdResponseCaptor;
  @Captor
  private ArgumentCaptor<BankAccountDetailsGrpcResponse> findByIbanResponseCaptor;

  private BankAccountLookupGrpcController service;

  @BeforeEach
  void setUp() {
    service = new BankAccountLookupGrpcController(getByIdQueryHandler, getByIbanQueryHandler, mapper);
  }

  @Test
  void shouldReturnMappedBankAccount_whenAccountIsFoundById() {
    var mappedAccount = BankAccount.newBuilder().setId(BANK_ACCOUNT_ID.toString()).build();
    var request = FindBankAccountByIdRequest.newBuilder().setBankAccountId(BANK_ACCOUNT_ID.toString()).build();
    when(getByIdQueryHandler.handle(new GetBankAccountByIdQuery(new com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId(BANK_ACCOUNT_ID))))
      .thenReturn(summary);
    when(mapper.toResponse(summary)).thenReturn(mappedAccount);

    service.findById(request, findByIdResponseObserver);

    verify(getByIdQueryHandler).handle(idQueryCaptor.capture());
    verify(mapper).toResponse(summary);
    verify(findByIdResponseObserver).onNext(findByIdResponseCaptor.capture());
    verify(findByIdResponseObserver).onCompleted();

    var softly = new SoftAssertions();
    softly.assertThat(idQueryCaptor.getValue().bankAccountId().value()).isEqualTo(BANK_ACCOUNT_ID);
    softly.assertThat(findByIdResponseCaptor.getValue().getBankAccount()).isEqualTo(mappedAccount);
    softly.assertAll();
  }

  @Test
  void shouldReturnMappedBankAccount_whenAccountIsFoundByIban() {
    var mappedAccount = BankAccount.newBuilder().setId(BANK_ACCOUNT_ID.toString()).build();
    var request = FindBankAccountByIbanRequest.newBuilder().setIban(IBAN).build();
    when(getByIbanQueryHandler.handle(new GetBankAccountByIbanQuery(com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban.of(IBAN))))
      .thenReturn(summary);
    when(mapper.toResponse(summary)).thenReturn(mappedAccount);

    service.findByIban(request, findByIbanResponseObserver);

    verify(getByIbanQueryHandler).handle(ibanQueryCaptor.capture());
    verify(mapper).toResponse(summary);
    verify(findByIbanResponseObserver).onNext(findByIbanResponseCaptor.capture());
    verify(findByIbanResponseObserver).onCompleted();

    var softly = new SoftAssertions();
    softly.assertThat(ibanQueryCaptor.getValue().iban().value()).isEqualTo(IBAN);
    softly.assertThat(findByIbanResponseCaptor.getValue().getBankAccount()).isEqualTo(mappedAccount);
    softly.assertAll();
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"not-a-uuid", "1-1-1-1-1", "  "})
  void shouldRejectRequest_whenBankAccountIdIsInvalid(String bankAccountId) {
    var requestBuilder = FindBankAccountByIdRequest.newBuilder();
    if (bankAccountId != null) {
      requestBuilder.setBankAccountId(bankAccountId);
    }

    assertThatThrownBy(() -> service.findById(requestBuilder.build(), findByIdResponseObserver))
      .isInstanceOf(InvalidGrpcRequestException.class)
      .hasMessage(BankAccountLookupGrpcController.INVALID_BANK_ACCOUNT_ID);

    verifyNoInteractions(getByIdQueryHandler, getByIbanQueryHandler, mapper, findByIdResponseObserver);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"not-an-iban", "  "})
  void shouldRejectRequest_whenIbanIsInvalid(String iban) {
    var requestBuilder = FindBankAccountByIbanRequest.newBuilder();
    if (iban != null) {
      requestBuilder.setIban(iban);
    }

    assertThatThrownBy(() -> service.findByIban(requestBuilder.build(), findByIbanResponseObserver))
      .isInstanceOf(InvalidGrpcRequestException.class)
      .hasMessage(BankAccountLookupGrpcController.INVALID_IBAN);

    verifyNoInteractions(getByIdQueryHandler, getByIbanQueryHandler, mapper, findByIbanResponseObserver);
  }
}
