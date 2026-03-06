package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountNotActiveExceptionTest {

  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();

//  @Test
//  void shouldCreateExceptionWithProvidedMessageCodeAndTitle_whenParametersAreValid() {
//    var bankAccountId = BankAccountId.of(BANK_ACCOUNT_UUID);
//
//    var messageCode = "recipient.cannotBeDeleted.bankAccountNotActive";
//    var title = "Recipient cannot be deleted";
//
//    var exception =
//        new BankAccountNotActiveException(
//            messageCode,
//            title,
//            bankAccountId.value()
//        );
//
//    assertThat(exception)
//        .isInstanceOf(DomainException.class)
//        .satisfies(
//            e -> {
//              assertThat(e.messageCode()).isEqualTo(messageCode);
//              assertThat(e.title()).isEqualTo(title);
//              assertThat(e.getCause()).isNull();
//              assertThat(e.getMessage()).isEqualTo(messageCode);
//              assertThat(e.args())
//                  .hasSize(1)
//                  .containsExactly(BANK_ACCOUNT_UUID);
//            });
//  }
}