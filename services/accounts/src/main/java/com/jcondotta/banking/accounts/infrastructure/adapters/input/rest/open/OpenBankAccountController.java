package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open;

import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import com.jcondotta.banking.infrastructure.adapters.input.rest.http.HttpHeadersConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.UUID;

@RequestMapping("${api.bank-accounts.root-path}")
public interface OpenBankAccountController {

  @Operation(
    tags = {"bank accounts"},
    summary = "Open a new bank account",
    description = "Opens a new bank account. X-Idempotency-Key is required; repeat the same key and data to replay the original 201 Location. Different data for the same key returns 409.",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Primary account holder and bank account opening details",
      required = true,
      content = @Content(
        mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = OpenBankAccountRequest.class)
      )
    )
  )
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> openBankAccount(
    @Valid @RequestBody OpenBankAccountRequest openBankAccountRequest,
    @RequestHeader(HttpHeadersConstants.IDEMPOTENCY_KEY) UUID idempotencyKey
  );
}
