package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.GetBankAccountByIdQuery;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.mapper.BankAccountLookupResponseControllerMapper;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.BankAccountDetailsResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class BankAccountLookupControllerImpl implements BankAccountLookupController {

  private final QueryHandler<GetBankAccountByIdQuery, BankAccountSummary> queryHandler;
  private final BankAccountLookupResponseControllerMapper mapper;

  public ResponseEntity<BankAccountDetailsResponse> getBankAccount(UUID bankAccountId) {
    var bankAccountSummary = queryHandler.handle(new GetBankAccountByIdQuery(new BankAccountId(bankAccountId)));
    return ResponseEntity.ok(mapper.toBankAccountDetailsResponse(bankAccountSummary));
  }
}
