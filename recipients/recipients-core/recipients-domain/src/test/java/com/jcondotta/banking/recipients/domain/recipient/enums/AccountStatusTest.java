package com.jcondotta.banking.recipients.domain.recipient.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class AccountStatusTest {

  @ParameterizedTest
  @EnumSource(AccountStatus.class)
  void shouldReturnTrueForIsActive_onlyWhenStatusIsActive(AccountStatus status) {
    assertThat(status.isActive()).isEqualTo(status == AccountStatus.ACTIVE);
  }
}
