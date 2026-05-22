package com.jcondotta.banking.transfers.infrastructure.adapters.output.bank_account;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/api/bank-accounts")
public interface BankAccountLookupClient {

  @GetExchange
  BankAccountLookupResponse findByIban(@RequestParam("iban") String iban);
}