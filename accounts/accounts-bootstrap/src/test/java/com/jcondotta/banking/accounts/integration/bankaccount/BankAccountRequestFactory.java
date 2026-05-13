package com.jcondotta.banking.accounts.integration.bankaccount;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.*;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.AccountTypeRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.CurrencyRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;

public final class BankAccountRequestFactory {

  private BankAccountRequestFactory() {
  }

  public static OpenBankAccountRequest openBankAccount(
    AccountType accountType,
    Currency currency,
    AccountHolderFixtures fixture
  ) {
    return new OpenBankAccountRequest(
      AccountTypeRequest.valueOf(accountType.name()),
      CurrencyRequest.valueOf(currency.name()),
      accountHolder(fixture)
    );
  }

  public static AddJointHolderRequest addJointHolder(AccountHolderFixtures fixture) {
    var holder = accountHolder(fixture);
    return new AddJointHolderRequest(
      holder.personalInfo(),
      holder.contactInfo(),
      holder.address()
    );
  }

  private static AccountHolderRequest accountHolder(AccountHolderFixtures fixture) {
    var personalInfo = fixture.personalInfo();
    var contactInfo = fixture.contactInfo();
    var address = fixture.address();
    var identityDocument = personalInfo.identityDocument();

    return new AccountHolderRequest(
      new PersonalInfoRequest(
        personalInfo.holderName().firstName(),
        personalInfo.holderName().lastName(),
        new IdentityDocumentRequest(
          DocumentTypeRequest.valueOf(identityDocument.type().name()),
          DocumentCountryRequest.valueOf(identityDocument.country().name()),
          identityDocument.number().value()
        ),
        personalInfo.dateOfBirth().value()
      ),
      new ContactInfoRequest(
        contactInfo.email().value(),
        contactInfo.phoneNumber().value()
      ),
      new AddressRequest(
        address.street().value(),
        address.streetNumber().value(),
        address.addressComplement() == null ? null : address.addressComplement().value(),
        address.postalCode().value(),
        address.city().value()
      )
    );
  }
}
