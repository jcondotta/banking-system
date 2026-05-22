package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup;

import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.BankAccountDetailsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

public interface BankAccountLookupController {

  @Operation(
    tags = {"bank accounts"},
    summary = "Get bank account details",
    description = "Retrieves the details of a bank account identified by its bank account id."
  )
  @GetMapping(value = "${api.bank-accounts.bank-account-id-path}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BankAccountDetailsResponse> getBankAccountById(
    @Parameter(description = "Unique identifier of the bank account", required = true, example = "01920bff-1338-7efd-ade6-e9128debe5d4")
    @PathVariable("bank-account-id") UUID bankAccountId);

  @Operation(
    tags = {"bank accounts"},
    summary = "Get bank account details by IBAN",
    description = "Retrieves the details of a bank account identified by its IBAN."
  )
  @GetMapping(value = "${api.bank-accounts.root-path}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<BankAccountDetailsResponse> getBankAccountByIban(
    @Parameter(description = "International Bank Account Number", required = true, example = "GB29NWBK60161331926819")
    @RequestParam("iban") String iban);
}
