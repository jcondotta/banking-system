package com.jcondotta.banking.accounts.application.bankaccount.command.open.model;

import com.jcondotta.application.command.Command;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.PersonalInfo;

import static java.util.Objects.requireNonNull;

public record OpenBankAccountCommand(
  PersonalInfo personalInfo,
  ContactInfo contactInfo,
  Address address,
  AccountType accountType,
  Currency currency
) implements Command<BankAccountId> {

  static final String PERSONAL_INFO_REQUIRED = "personalInfo must be provided";
  static final String CONTACT_INFO_REQUIRED = "contactInfo must be provided";
  static final String ADDRESS_REQUIRED = "address must be provided";
  static final String ACCOUNT_TYPE_REQUIRED = "accountType must be provided";
  static final String CURRENCY_REQUIRED = "currency must be provided";

  public OpenBankAccountCommand {
    requireNonNull(personalInfo, PERSONAL_INFO_REQUIRED);
    requireNonNull(contactInfo, CONTACT_INFO_REQUIRED);
    requireNonNull(address, ADDRESS_REQUIRED);
    requireNonNull(accountType, ACCOUNT_TYPE_REQUIRED);
    requireNonNull(currency, CURRENCY_REQUIRED);
  }
}