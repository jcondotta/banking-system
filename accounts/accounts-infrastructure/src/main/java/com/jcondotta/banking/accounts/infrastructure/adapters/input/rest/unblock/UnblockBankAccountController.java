package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.unblock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@RequestMapping("${api.v1.bank-accounts.bank-account-id-path}")
public interface UnblockBankAccountController {

  @Operation(
    tags = {"bank accounts"},
    summary = "Unblock a bank account",
    description = "Unblocks a blocked bank account, restoring it to active status."
  )
  @PatchMapping("/unblock")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  ResponseEntity<Void> unblock(
    @Parameter(
      description = "Unique identifier of the bank account",
      required = true,
      example = "01920bff-1338-7efd-ade6-e9128debe5d4"
    )
    @PathVariable("bank-account-id") UUID bankAccountId
  );
}