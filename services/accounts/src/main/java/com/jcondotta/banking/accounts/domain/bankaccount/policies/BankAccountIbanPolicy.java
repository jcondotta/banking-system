package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountIbanConfigurationException;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import jakarta.annotation.Nullable;

public final class BankAccountIbanPolicy {

  private BankAccountIbanPolicy() {}

  public static void validate(@Nullable Iban iban, AccountStatus accountStatus) {
    var hasIban = iban != null;
    if (accountStatus.isPending() == hasIban) {
      throw new InvalidBankAccountIbanConfigurationException(accountStatus);
    }
  }
}
