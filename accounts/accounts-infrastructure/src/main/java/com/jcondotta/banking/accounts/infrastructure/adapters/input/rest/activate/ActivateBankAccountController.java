package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.activate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("${api.v1.bank-accounts.bank-account-id-path}")
public interface ActivateBankAccountController {

  @Operation(
    tags = {"bank accounts"},
    summary = "Activate a new bank account",
    description = "Activates a pending bank account."
  )
  @PatchMapping("/activate")
  ResponseEntity<Void> activateBankAccount(
    @Parameter(description = "Unique identifier of the bank account", required = true, example = "01920bff-1338-7efd-ade6-e9128debe5d4")
    @PathVariable("bank-account-id") UUID bankAccountId);
}
