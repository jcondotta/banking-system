package com.jcondotta.banking.transfers.infrastructure.adapters.output.bank_account;

import com.jcondotta.banking.transfers.application.bank_account.ports.output.BankAccountLookupPort;
import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_account.value_objects.Iban;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class BankAccountLookupAdapter implements BankAccountLookupPort {

  private final BankAccountLookupClient bankAccountLookupClient;

  @Override
  public Optional<BankAccountId> findByIban(Iban iban) {
    try {
      var response = bankAccountLookupClient.findByIban(iban.value());

      return Optional.ofNullable(response)
        .map(BankAccountLookupResponse::id)
        .map(BankAccountId::of);
    }
    catch (HttpClientErrorException.NotFound ex) {
      return Optional.empty();
    }
  }
}
