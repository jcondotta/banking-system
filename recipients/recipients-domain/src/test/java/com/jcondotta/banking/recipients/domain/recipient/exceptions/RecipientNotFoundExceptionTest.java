package com.jcondotta.banking.recipients.domain.recipient.exceptions;

class RecipientNotFoundExceptionTest {

//  private static final UUID BANK_ACCOUNT_UUID = UUID.randomUUID();
//  private static final UUID ACCOUNT_RECIPIENT_UUID = UUID.randomUUID();
//
//  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(BANK_ACCOUNT_UUID);
//  private static final RecipientId RECIPIENT_ID = RecipientId.of(ACCOUNT_RECIPIENT_UUID);
//
//  @Test
//  void shouldExposeExpectedMetadata_whenCreatedWithoutCause() {
//    var exception = new RecipientNotFoundException(BANK_ACCOUNT_ID, RECIPIENT_ID);
//
//    assertThat(exception)
//        .isInstanceOf(DomainObjectNotFoundException.class)
//        .hasMessage(RecipientNotFoundException.RECIPIENT_NOT_FOUND_TEMPLATE)
//        .satisfies(
//            e -> {
//              assertThat(e.title())
//                  .isEqualTo(RecipientNotFoundException.RECIPIENT_NOT_FOUND_TITLE);
//              assertThat(e.messageCode())
//                  .isEqualTo(RecipientNotFoundException.RECIPIENT_NOT_FOUND_TEMPLATE);
//
//              assertThat(e.args())
//                  .hasSize(2)
//                  .containsExactly(BANK_ACCOUNT_UUID, ACCOUNT_RECIPIENT_UUID);
//
//              assertThat(e.getCause()).isNull();
//            });
//  }
//
//  @Test
//  void shouldExposeExpectedMetadata_whenCreatedWithCause() {
//    var rootCause = new RuntimeException("404 simulated");
//    var exception = new RecipientNotFoundException(BANK_ACCOUNT_ID, RECIPIENT_ID, rootCause);
//
//    assertThat(exception)
//        .isInstanceOf(DomainObjectNotFoundException.class)
//        .hasMessage(RecipientNotFoundException.RECIPIENT_NOT_FOUND_TEMPLATE)
//        .satisfies(
//            e -> {
//              assertThat(e.title())
//                  .isEqualTo(RecipientNotFoundException.RECIPIENT_NOT_FOUND_TITLE);
//              assertThat(e.messageCode())
//                  .isEqualTo(RecipientNotFoundException.RECIPIENT_NOT_FOUND_TEMPLATE);
//
//              assertThat(e.args())
//                  .hasSize(2)
//                  .containsExactly(BANK_ACCOUNT_UUID, ACCOUNT_RECIPIENT_UUID);
//
//              assertThat(e.getCause()).isSameAs(rootCause);
//            });
//  }
}
