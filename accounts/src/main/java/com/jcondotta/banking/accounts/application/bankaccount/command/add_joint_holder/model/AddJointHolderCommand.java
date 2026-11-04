package com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model;

import com.jcondotta.application.command.Command;
import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyKey;
import com.jcondotta.banking.accounts.application.common.idempotency.IdempotentCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.PersonalInfo;

import static java.util.Objects.requireNonNull;

public record AddJointHolderCommand(
  BankAccountId bankAccountId,
  PersonalInfo personalInfo,
  ContactInfo contactInfo,
  Address address,
  IdempotencyKey idempotencyKey
) implements Command<Void>, IdempotentCommand {

  static final String BANK_ACCOUNT_ID_REQUIRED = "bankAccountId must be provided";
  static final String PERSONAL_INFO_REQUIRED = "personalInfo must be provided";
  static final String CONTACT_INFO_REQUIRED = "contactInfo must be provided";
  static final String ADDRESS_REQUIRED = "address must be provided";
  static final String IDEMPOTENCY_KEY_REQUIRED = "idempotencyKey must be provided";

  public AddJointHolderCommand {
    requireNonNull(bankAccountId, BANK_ACCOUNT_ID_REQUIRED);
    requireNonNull(personalInfo, PERSONAL_INFO_REQUIRED);
    requireNonNull(contactInfo, CONTACT_INFO_REQUIRED);
    requireNonNull(address, ADDRESS_REQUIRED);
    requireNonNull(idempotencyKey, IDEMPOTENCY_KEY_REQUIRED);
  }

}
