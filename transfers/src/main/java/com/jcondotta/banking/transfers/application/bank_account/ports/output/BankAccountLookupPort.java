package com.jcondotta.banking.transfers.application.bank_account.ports.output;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_account.value_objects.Iban;

import java.util.Optional;

public interface BankAccountLookupPort {

  Optional<BankAccountId> findByIban(Iban iban);
}
