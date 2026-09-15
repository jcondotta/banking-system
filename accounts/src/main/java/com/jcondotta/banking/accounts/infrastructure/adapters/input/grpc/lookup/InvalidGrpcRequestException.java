package com.jcondotta.banking.accounts.infrastructure.adapters.input.grpc.lookup;

final class InvalidGrpcRequestException extends RuntimeException {

  InvalidGrpcRequestException(String message) {
    super(message);
  }
}
