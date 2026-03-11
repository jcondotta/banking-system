package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount;

import com.jcondotta.application.core.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.GetBankAccountByIdQuery;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount.mapper.BankAccountLookupResponseControllerMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
public class BankAccountLookupControllerImpl implements BankAccountLookupController {

  private final QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> queryHandler;
  private final BankAccountLookupResponseControllerMapper mapper;

  public ResponseEntity<BankAccountDetailsResponse> getBankAccount(UUID bankAccountId) {
    log.info("Received request to retrieve bank account [id={}]", bankAccountId);

    var bankAccountSummary = queryHandler.handle(new GetBankAccountByIdQuery(new BankAccountId(bankAccountId)));
    return ResponseEntity.ok(mapper.toBankAccountDetailsResponse(bankAccountSummary));
  }
}