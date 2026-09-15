package com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.lookup;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.GetBankAccountByIbanQuery;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.GetBankAccountByIdQuery;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.BankAccountQueryServiceGrpc;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.FindBankAccountByIbanRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.FindBankAccountByIbanResponse;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.FindBankAccountByIdRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.generated.v1.FindBankAccountByIdResponse;
import com.jcondotta.domain.exception.DomainValidationException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class BankAccountQueryGrpcService extends BankAccountQueryServiceGrpc.BankAccountQueryServiceImplBase {

  static final String INVALID_BANK_ACCOUNT_ID = "bank_account_id must be a valid UUID";
  static final String INVALID_IBAN = "iban must be a valid IBAN";

  private final QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> getByIdQueryHandler;
  private final QueryHandler<GetBankAccountByIbanQuery, BankAccountSummary> getByIbanQueryHandler;
  private final BankAccountGrpcMapper mapper;

  @Override
  public void findById(FindBankAccountByIdRequest request, StreamObserver<FindBankAccountByIdResponse> responseObserver) {
    var query = new GetBankAccountByIdQuery(BankAccountId.of(parseBankAccountId(request.getBankAccountId())));
    var response = FindBankAccountByIdResponse.newBuilder()
      .setBankAccount(mapper.toResponse(getByIdQueryHandler.handle(query)))
      .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void findByIban(FindBankAccountByIbanRequest request, StreamObserver<FindBankAccountByIbanResponse> responseObserver) {
    var query = new GetBankAccountByIbanQuery(parseIban(request.getIban()));
    var response = FindBankAccountByIbanResponse.newBuilder()
      .setBankAccount(mapper.toResponse(getByIbanQueryHandler.handle(query)))
      .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private static UUID parseBankAccountId(String value) {
    try {
      var bankAccountId = UUID.fromString(value);
      if (!bankAccountId.toString().equalsIgnoreCase(value)) {
        throw new IllegalArgumentException();
      }
      return bankAccountId;
    }
    catch (IllegalArgumentException exception) {
      throw new InvalidGrpcRequestException(INVALID_BANK_ACCOUNT_ID);
    }
  }

  private static Iban parseIban(String value) {
    try {
      return Iban.of(value);
    }
    catch (DomainValidationException exception) {
      throw new InvalidGrpcRequestException(INVALID_IBAN);
    }
  }
}
