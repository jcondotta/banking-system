package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open;

import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("http://localhost:8080/api/v1/bank-accounts")
public interface BankAccountsHttpClient {

  @PostExchange
  void create(@RequestBody OpenBankAccountRequest request);
}