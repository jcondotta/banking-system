package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import java.util.UUID;

class BankAccountNotFoundExceptionTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();

//  @Test
//  void shouldCreateExceptionWithCauseCorrectly_whenBankAccountIdIsValid() {
//    var bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID);
//    var rootCause = new RuntimeException("404 simulated");
//    var exception = new BankAccountNotFoundException(bankAccountId, rootCause);
//
//    assertThat(exception)
//        .isInstanceOf(DomainObjectNotFoundException.class)
//        .hasMessage(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TEMPLATE)
//        .satisfies(
//            e -> {
//              assertThat(e.title()).isEqualTo(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TITLE);
//              assertThat(e.getCause()).isSameAs(rootCause);
//              assertThat(e.messageCode()).isEqualTo(BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND_TEMPLATE);
//              assertThat(e.args())
//                  .hasSize(1)
//                  .containsExactly(BANK_ACCOUNT_UUID);
//            });
//  }
}
