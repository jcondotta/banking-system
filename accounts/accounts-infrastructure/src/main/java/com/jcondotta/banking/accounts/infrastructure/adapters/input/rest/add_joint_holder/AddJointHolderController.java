package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder;

import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("${api.v1.bank-accounts.bank-account-id-path}")
public interface AddJointHolderController {

  @Operation(
    tags = {"Account holders"},
    summary = "Create joint account holders",
    description = "Add joint account holder to an existing bank account.",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "Details of joint account holders to be added",
      required = true,
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = AddJointHolderRequest.class)
      )
    )
  )
  @PostMapping(value = "/account-holders", consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> createJointAccountHolder(
    @Parameter(description = "Unique identifier of the bank account", required = true, example = "01920bff-1338-7efd-ade6-e9128debe5d4")
    @PathVariable("bank-account-id") UUID bankAccountId,
    @Valid @RequestBody AddJointHolderRequest request);
}