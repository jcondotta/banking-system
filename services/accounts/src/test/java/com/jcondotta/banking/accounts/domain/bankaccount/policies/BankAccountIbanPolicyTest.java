package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountIbanConfigurationException;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountIbanPolicyTest {

  private static final Iban VALID_IBAN = Iban.of("ES3801283316232166447417");

  @Test
  void shouldNotThrowAnyException_whenStatusIsPendingAndIbanIsAbsent() {
    assertThatCode(() -> BankAccountIbanPolicy.validate(null, AccountStatus.PENDING))
      .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
  void shouldNotThrowAnyException_whenStatusIsNotPendingAndIbanIsPresent(AccountStatus accountStatus) {
    assertThatCode(() -> BankAccountIbanPolicy.validate(VALID_IBAN, accountStatus))
      .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowInvalidBankAccountIbanConfigurationException_whenStatusIsPendingAndIbanIsPresent() {
    assertThatThrownBy(() -> BankAccountIbanPolicy.validate(VALID_IBAN, AccountStatus.PENDING))
      .isInstanceOf(InvalidBankAccountIbanConfigurationException.class)
      .hasMessage("Bank account with PENDING status must not have an IBAN");
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, names = "PENDING", mode = EnumSource.Mode.EXCLUDE)
  void shouldThrowInvalidBankAccountIbanConfigurationException_whenStatusIsNotPendingAndIbanIsAbsent(AccountStatus accountStatus) {
    assertThatThrownBy(() -> BankAccountIbanPolicy.validate(null, accountStatus))
      .isInstanceOf(InvalidBankAccountIbanConfigurationException.class)
      .hasMessage("Bank account with " + accountStatus + " status must have an IBAN");
  }
}
