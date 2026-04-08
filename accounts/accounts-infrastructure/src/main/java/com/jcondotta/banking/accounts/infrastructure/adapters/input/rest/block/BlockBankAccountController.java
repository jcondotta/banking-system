package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.block;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("${api.v1.bank-accounts.bank-account-id-path}")
public interface BlockBankAccountController {

  @Operation(
    tags = {"bank accounts"},
    summary = "Block a bank account",
    description = "Blocks an active bank account, preventing further operations."
  )
  @PatchMapping("/block")
  ResponseEntity<Void> block(
    @Parameter(description = "Unique identifier of the bank account", required = true, example = "01920bff-1338-7efd-ade6-e9128debe5d4")
    @PathVariable("bank-account-id") UUID bankAccountId
  );
}
