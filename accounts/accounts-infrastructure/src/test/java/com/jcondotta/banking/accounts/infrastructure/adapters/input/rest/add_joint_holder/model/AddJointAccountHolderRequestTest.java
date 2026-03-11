package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model;

class AddJointAccountHolderRequestTest {

//  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();
//
//  private static final String VALID_NAME = AccountHolderFixtures.JEFFERSON.getAccountHolderName().value();
//  private static final String VALID_PASSPORT = AccountHolderFixtures.JEFFERSON.getPassportNumber().value();
//  private static final LocalDate VALID_DATE_OF_BIRTH = AccountHolderFixtures.JEFFERSON.getDateOfBirth().value();
//  private static final String VALID_EMAIL = AccountHolderFixtures.JEFFERSON.getEmail().value();
//
//  @Test
//  void shouldNotDetectConstraintViolation_whenRequestIsValid() {
//    var request = new AddJointAccountHolderRequest(
//      VALID_NAME,
//      VALID_PASSPORT,
//      VALID_DATE_OF_BIRTH,
//      VALID_EMAIL
//    );
//
//    assertThat(VALIDATOR.validate(request)).isEmpty();
//  }
//
//  @ParameterizedTest
//  @ArgumentsSource(BlankValuesArgumentProvider.class)
//  void shouldDetectConstraintViolation_whenAccountHolderNameIsBlank(String blankName) {
//    var request = new AddJointAccountHolderRequest(
//      blankName,
//      VALID_PASSPORT,
//      VALID_DATE_OF_BIRTH,
//      VALID_EMAIL
//    );
//
//    assertThat(VALIDATOR.validate(request))
//      .hasSize(1)
//      .first()
//      .satisfies(violation ->
//        assertThat(violation.getPropertyPath()).hasToString("holderName")
//      );
//  }
//
//  @Test
//  void shouldDetectConstraintViolation_whenAccountHolderNameExceedsMaxLength() {
//    var longName = "A".repeat(AccountHolderName.MAX_LENGTH + 1);
//
//    var request = new AddJointAccountHolderRequest(
//      longName,
//      VALID_PASSPORT,
//      VALID_DATE_OF_BIRTH,
//      VALID_EMAIL
//    );
//
//    assertThat(VALIDATOR.validate(request))
//      .hasSize(1)
//      .first()
//      .satisfies(violation ->
//        assertThat(violation.getPropertyPath()).hasToString("holderName")
//      );
//  }
//
//  @Test
//  void shouldDetectConstraintViolation_whenPassportNumberIsNull() {
//    var request = new AddJointAccountHolderRequest(
//      VALID_NAME,
//      null,
//      VALID_DATE_OF_BIRTH,
//      VALID_EMAIL
//    );
//
//    assertThat(VALIDATOR.validate(request))
//      .hasSize(1)
//      .first()
//      .satisfies(violation ->
//        assertThat(violation.getPropertyPath()).hasToString("passportNumber")
//      );
//  }
//
//  @Test
//  void shouldDetectConstraintViolation_whenPassportNumberLengthIsInvalid() {
//    var invalidPassport = "A".repeat(PassportNumber.LENGTH - 1);
//
//    var request = new AddJointAccountHolderRequest(
//      VALID_NAME,
//      invalidPassport,
//      VALID_DATE_OF_BIRTH,
//      VALID_EMAIL
//    );
//
//    assertThat(VALIDATOR.validate(request))
//      .hasSize(1)
//      .first()
//      .satisfies(violation ->
//        assertThat(violation.getPropertyPath()).hasToString("passportNumber")
//      );
//  }
//
//  @Test
//  void shouldDetectConstraintViolation_whenDateOfBirthIsNull() {
//    var request = new AddJointAccountHolderRequest(
//      VALID_NAME,
//      VALID_PASSPORT,
//      null,
//      VALID_EMAIL
//    );
//
//    assertThat(VALIDATOR.validate(request))
//      .hasSize(1)
//      .first()
//      .satisfies(violation ->
//        assertThat(violation.getPropertyPath()).hasToString("dateOfBirth")
//      );
//  }
//
//  @Test
//  void shouldDetectConstraintViolation_whenDateOfBirthIsInTheFuture() {
//    var futureDate = LocalDate.now().plusDays(1);
//
//    var request = new AddJointAccountHolderRequest(
//      VALID_NAME,
//      VALID_PASSPORT,
//      futureDate,
//      VALID_EMAIL
//    );
//
//    assertThat(VALIDATOR.validate(request))
//      .hasSize(1)
//      .first()
//      .satisfies(violation ->
//        assertThat(violation.getPropertyPath()).hasToString("dateOfBirth")
//      );
//  }
//
////  @Test
////  void shouldDetectConstraintViolation_whenEmailFormatIsInvalid() {
////    var invalidEmail = "invalid-email-format";
////
////    var request = new AddJointAccountHolderRequest(
////      VALID_NAME,
////      VALID_PASSPORT,
////      VALID_DATE_OF_BIRTH,
////      invalidEmail
////    );
////
////    assertThat(VALIDATOR.validate(request))
////      .hasSize(1)
////      .first()
////      .satisfies(violation ->
////        assertThat(violation.getPropertyPath()).hasToString("email")
////      );
////  }
//
//  @ParameterizedTest
//  @ArgumentsSource(BlankValuesArgumentProvider.class)
//  void shouldDetectConstraintViolation_whenEmailIsBlank(String blankEmail) {
//    var request = new AddJointAccountHolderRequest(
//      VALID_NAME,
//      VALID_PASSPORT,
//      VALID_DATE_OF_BIRTH,
//      blankEmail
//    );
//
//    assertThat(VALIDATOR.validate(request))
//      .hasSize(1)
//      .first()
//      .satisfies(violation ->
//        assertThat(violation.getPropertyPath()).hasToString("email")
//      );
//  }
//
////  @ParameterizedTest
////  @ValueSource(strings = {
////    "plainaddress",
////    "missingatsign.com",
////    "@missinglocal.com",
////    "missingdomain@",
////    "missingdot@domain",
////  })
////  void shouldDetectConstraintViolation_whenEmailFormatIsInvalid(String invalidEmail) {
////    var request = new AddJointAccountHolderRequest(
////      VALID_NAME,
////      VALID_PASSPORT,
////      VALID_DATE_OF_BIRTH,
////      invalidEmail
////    );
////
////    assertThat(VALIDATOR.validate(request))
////      .hasSize(1)
////      .first()
////      .satisfies(violation ->
////        assertThat(violation.getPropertyPath()).hasToString("email")
////      );
////  }
//
//  @Test
//  void shouldDetectConstraintViolation_whenEmailExceedsMaxLength() {
//    var longEmail = "a".repeat(
//      com.jcondotta.bankaccounts.domain.value_objects.contact.Email.MAX_LENGTH + 1
//    ) + "@email.com";
//
//    var request = new AddJointAccountHolderRequest(
//      VALID_NAME,
//      VALID_PASSPORT,
//      VALID_DATE_OF_BIRTH,
//      longEmail
//    );
//
//    assertThat(VALIDATOR.validate(request))
//      .hasSize(1)
//      .first()
//      .satisfies(violation ->
//        assertThat(violation.getPropertyPath()).hasToString("email")
//      );
//  }
}
