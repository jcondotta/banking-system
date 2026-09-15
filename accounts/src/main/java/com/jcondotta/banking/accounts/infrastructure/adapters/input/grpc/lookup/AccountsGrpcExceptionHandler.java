package com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.lookup;

import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import io.grpc.Status;
import io.grpc.StatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

@Configuration(proxyBeanMethods = false)
class AccountsGrpcExceptionHandler {

  static final String BANK_ACCOUNT_NOT_FOUND = "Bank account not found";
  static final String INTERNAL_ERROR = "Internal server error";

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountsGrpcExceptionHandler.class);

  @Bean
  GrpcExceptionHandler grpcExceptionHandler() {
    return exception -> switch (exception) {
      case InvalidGrpcRequestException ex -> statusException(Status.INVALID_ARGUMENT, ex.getMessage());
      case BankAccountNotFoundException ignored -> statusException(Status.NOT_FOUND, BANK_ACCOUNT_NOT_FOUND);
      default -> internalError(exception);
    };
  }

  private static StatusException internalError(Throwable exception) {
    LOGGER.error("Unexpected error while processing gRPC request", exception);
    return statusException(Status.INTERNAL, INTERNAL_ERROR);
  }

  private static StatusException statusException(Status status, String description) {
    return status.withDescription(description).asException();
  }
}
